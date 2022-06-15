package com.example.antriankesehatan.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentChangeProfileBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangeProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        preference = SharedPreference(requireActivity())


        val listGender = listOf("Laki-laki", "Perempuan")
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_gender, listGender)
        (binding.edtLayoutGender.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.btnSaveChanges.setOnClickListener {
            updateProfile()
        }

    }

    private fun updateProfile() {
        val token = preference.getToken()
        val name = binding.edtName.text.toString()
        val telp = binding.edtTelp.text.toString()
        val gender = binding.edtGender.text.toString()
        val address = binding.edtAddress.text.toString()

        NetworkConfig().getApiService().updateProfile(
            "application/json",
            "Bearer $token",
            name,
            telp,
            gender,
            address
        ).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>,
            ) {
                Log.d("respone======", response.message())
                if (response.isSuccessful) {
                    Toast.makeText(requireActivity(), "Update profil berhasil", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireActivity(), "Update profil gagal", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
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