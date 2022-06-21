package com.example.antriankesehatan.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentUserBinding
import com.example.antriankesehatan.model.LogoutResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.ui.auth.login.LoginActivity
import com.example.antriankesehatan.utils.SharedPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var preference: SharedPreference

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
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]


        binding?.apply {
            btnMyProfile.setOnClickListener {
                navigateToChangeProfile()
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


    private fun navigateToChangeProfile() {
        val bundle = Bundle()
        bundle.putString("nama", "value")
        bundle.putString("nama", "value")
        bundle.putString("nama", "value")
        bundle.putString("nama", "value")

        findNavController().navigate(R.id.action_profileFragment_to_changeProfileFragment, bundle)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }


}