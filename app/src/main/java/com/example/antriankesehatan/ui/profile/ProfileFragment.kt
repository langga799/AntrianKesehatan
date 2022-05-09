package com.example.antriankesehatan.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
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


        }
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