package com.example.antriankesehatan.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentChangeProfileBinding
import com.example.antriankesehatan.model.GetProfileResponse
import com.example.antriankesehatan.model.UpdateProfileResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangeProfileFragment : Fragment() {

    private var _binding: FragmentChangeProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var preference: SharedPreference
    private var listGender = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChangeProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        preference = SharedPreference(requireActivity())


        listGender = arrayListOf("Pria", "Wanita")
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_gender, listGender)
        (binding.edtLayoutGender.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        getProfile()

        binding.btnSaveChanges.setOnClickListener {
            updateProfile()
        }

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
                        binding.apply {
                            edtName.setText(profile?.name)
                            edtTelp.setText(profile?.noTlp)

                            when (profile?.jenisKelamin) {
                                "Pria" -> {
                                    binding.edtGender.setText(binding.edtGender.adapter.getItem(0)
                                        .toString(), false)
                                }
                                "Wanita" -> {
                                    binding.edtGender.setText(binding.edtGender.adapter.getItem(1)
                                        .toString(), false)
                                }
                            }
                            edtAddress.setText(profile?.alamat)
                            edtBpjs.setText(profile?.noBpjs)
                        }
                    }
                }

                override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {

                }

            })
    }


    private fun updateProfile() {
        val token = preference.getToken()
        val name = binding.edtName.text.toString()
        val telp = binding.edtTelp.text.toString()
        val gender = binding.edtGender.text.toString()
        val address = binding.edtAddress.text.toString()
        val bpjs = binding.edtBpjs.text.toString()

        if (bpjs.isEmpty()) {
            binding.edtBpjs.setText("-")
        }

        val builder = AlertDialog.Builder(requireActivity())
            .setView(R.layout.progress)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
        NetworkConfig().getApiService().updateProfile(
            "application/json",
            "Bearer $token",
            name,
            telp,
            gender,
            address,
            bpjs
        ).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>,
            ) {

                when (response.code()) {
                    200 -> {
                        dialog.dismiss()
                        Toast.makeText(requireActivity(),
                            response.body()?.meta?.message,
                            Toast.LENGTH_SHORT)
                            .show()
                        val navController =
                            activity?.findNavController(R.id.nav_host_fragment_activity_main)
                        val id = navController?.currentDestination?.id
                        if (id != null) {
                            navController.popBackStack(id, true)
                            navController.navigate(id)
                        }
                    }
                    401 -> {
                        dialog.dismiss()
                        Toast.makeText(requireActivity(),
                            response.body()?.meta?.message,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                    500 -> {
                        dialog.dismiss()
                        Toast.makeText(requireActivity(),
                            response.body()?.meta?.message,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }


            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_changeProfileFragment_to_profileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }


}