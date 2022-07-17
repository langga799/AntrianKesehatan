package com.example.antriankesehatan.ui.profile


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.antriankesehatan.R
import com.example.antriankesehatan.alarmManager.AlarmWorker
import com.example.antriankesehatan.alarmManager.AlarmReceiver
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

    private var urlPhoto = ""


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


        workManager = WorkManager.getInstance(requireActivity())
        periodicWorkRequest =
            PeriodicWorkRequest.Builder(AlarmWorker::class.java, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MINUTES)
                .build()

        val alarmToggle = binding?.switchAlarm
        alarmToggle?.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked){
                true -> setAlarmNotification()
                false -> cancelAlarmNotification()
            }
        }

        Log.d("uuuuuuuuu", preference.getStateWorkAlarm().toString())
        when(preference.getStateWorkAlarm()){
            true -> alarmToggle?.isChecked = true
            false -> alarmToggle?.isChecked = false
        }

        // =====================================================================================

        binding?.apply {
            btnMyProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changeProfileFragment)
            }
            btnImageProfileCamera.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("URL_PHOTO", urlPhoto)
                findNavController().navigate(R.id.action_profileFragment_to_changePhotoFragment, bundle)
            }
            btnImageProfileCircle.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("URL_PHOTO", urlPhoto)
                findNavController().navigate(R.id.action_profileFragment_to_changePhotoFragment, bundle)
            }
            btnLogout.setOnClickListener {
                logoutUser()
            }
        }

    }


    private fun setAlarmNotification() {
        workManager.enqueue(periodicWorkRequest)
        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
            .observe(viewLifecycleOwner){ workInfo ->
                val status = workInfo.state.name
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        preference.saveWorkAlarm(true)
    }

    private fun cancelAlarmNotification() {
        workManager.cancelWorkById(periodicWorkRequest.id)
        preference.saveWorkAlarm(false)
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

                            urlPhoto = profile.photoProfile
                        }

                    }

                }

                override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {

                }

            })
    }



    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }




}