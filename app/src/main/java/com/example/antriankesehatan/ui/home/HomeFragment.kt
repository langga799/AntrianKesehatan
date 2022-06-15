package com.example.antriankesehatan.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentHomeBinding
import com.example.antriankesehatan.ui.pesan.MessageActivity
import com.example.antriankesehatan.utils.SharedPreference

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireActivity())

        Log.d("tokennnnnnnnnnn", preference.getToken())

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}