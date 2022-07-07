package com.example.antriankesehatan.ui.profile


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.antriankesehatan.MainActivity
import com.example.antriankesehatan.R
import com.example.antriankesehatan.alarmManager.AlarmReceiver
import com.example.antriankesehatan.alarmManager.MyWorker
import com.example.antriankesehatan.databinding.FragmentUserBinding
import com.example.antriankesehatan.model.GetProfileResponse
import com.example.antriankesehatan.model.LogoutResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.ui.auth.login.LoginActivity
import com.example.antriankesehatan.utils.Helper
import com.example.antriankesehatan.utils.SharedPreference
import com.example.antriankesehatan.utils.loadImageCircle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding
    private lateinit var preference: SharedPreference

    private lateinit var alarmManager: AlarmManager
    private lateinit var notifManager: NotificationManager
    private lateinit var notifyPendingIntent: PendingIntent


    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        preference = SharedPreference(requireActivity())

        getProfile()


        // =====================================================================================
        workManager = WorkManager.getInstance(requireActivity())

        notifManager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        alarmManager = activity?.getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmToggle = binding?.switchAlarm

        val notifyIntent = Intent(requireActivity(), AlarmReceiver::class.java)
        val alarmUp = (
                PendingIntent.getBroadcast(requireActivity(),
                    100,
                    notifyIntent,
                    PendingIntent.FLAG_NO_CREATE) != null
                )

        alarmToggle?.isChecked = alarmUp

        notifyPendingIntent = PendingIntent.getBroadcast(requireActivity(),
            100,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmToggle?.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> setAlarm()
                false -> cancelAlarm()
            }
        }

        // =====================================================================================

        binding?.apply {
            btnMyProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changeProfileFragment)
            }
            btnImageProfileCamera.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changePhotoFragment)
            }
            btnImageProfileCircle.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changePhotoFragment)
            }
            btnLogout.setOnClickListener {
                logoutUser()
            }
        }

        binding?.tvPersonName?.setOnClickListener {
            // PERIODIC
            periodicWorkRequest =
                PeriodicWorkRequest.Builder(MyWorker::class.java, 2, TimeUnit.MINUTES)
                    .build()
            workManager.enqueue(periodicWorkRequest)
            workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
                .observe(viewLifecycleOwner){ workInfo ->
                    val status = workInfo.state.name
                    Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
                }

        // Cancel periodic
        //    workManager.cancelWorkById(periodicWorkRequest.id)

            // ONE TIME TASK
//            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
//                .build()
//            workManager.enqueue(oneTimeWorkRequest)
//            workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
//                .observe(viewLifecycleOwner){ workInfo ->
//                    val status = workInfo.state.name
//                    Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
//                }
        }
    }


    private fun logoutUser() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Logout User")
            .setMessage("Apakah Anda ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                requestLogout()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun requestLogout() {
        val token = preference.getToken()
        NetworkConfig().getApiService().requestLogout("Bearer $token")
            .enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(
                    call: Call<LogoutResponse>,
                    response: Response<LogoutResponse>,
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireActivity(), "Logout Success", Toast.LENGTH_SHORT)
                            .show()

                        preference.clear()

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000L)
                            activity?.startActivity(Intent(requireActivity(),
                                LoginActivity::class.java))
                            activity?.finishAffinity()
                        }

                    } else {
                        Toast.makeText(requireActivity(), "Logout gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getProfile() {
        val token = preference.getToken()
        NetworkConfig().getApiService().getProfile("Bearer $token")
            .enqueue(object : Callback<GetProfileResponse> {
                override fun onResponse(
                    call: Call<GetProfileResponse>,
                    response: Response<GetProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                        val profile = response.body()?.data
                        binding?.tvPersonName?.text = profile?.name
                        if (profile?.photoProfile == null) {
                            binding?.btnImageProfileCircle?.setImageDrawable(ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.ic_baseline_account_circle_24))
                        } else {
                            activity?.loadImageCircle(
                                (Helper.BASE_IMAGE_URL_USER + profile.photoProfile),
                                binding?.btnImageProfileCircle!!
                            )
                        }

                    }

                }

                override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {

                }

            })
    }


    private fun setAlarm() {
        val triggerTime: Long =
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES
        val repeatInterval: Long = AlarmManager.INTERVAL_FIFTEEN_MINUTES
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60_000,
            1_000,
            notifyPendingIntent
        )
        Toast.makeText(requireActivity(), "Pengingat Diatur", Toast.LENGTH_SHORT).show()
        deliverNotification(requireActivity())

//        val data = Data.Builder()
//            .putString(MyWorker.)


    }

    private fun cancelAlarm() {
        alarmManager.cancel(notifyPendingIntent)
        notifManager.cancelAll()
        Toast.makeText(requireActivity(), "Pengingat Dibatalkan", Toast.LENGTH_SHORT).show()
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    private fun deliverNotification(context: Context) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,
            AlarmReceiver.NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_doctor)
            .setContentTitle("Pengingat Antrian")
            .setContentText("Sebentar lagi giliran anda untuk periksa :) ")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)


        notificationManager.notify(AlarmReceiver.NOTIFICATION_ID, builder.build())

    }


}