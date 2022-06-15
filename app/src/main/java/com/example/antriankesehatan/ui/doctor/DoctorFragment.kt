package com.example.antriankesehatan.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentDoctorBinding
import com.example.antriankesehatan.model.DataItemDokter
import com.example.antriankesehatan.model.GetDoctorResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorFragment : Fragment() {

    private var _binding: FragmentDoctorBinding? = null
    private val binding get() = _binding!!

    private lateinit var preference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDoctorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = SharedPreference(requireActivity())

        binding.circleImageView.setOnClickListener {
            findNavController().navigate(R.id.action_doctorFragment_to_jadwalFragment)
        }


        getListDoctor()
    }

    private fun getListDoctor() {
        val token = preference.getToken()
        NetworkConfig().getApiService().getListDoctor("Bearer $token")
            .enqueue(object : Callback<GetDoctorResponse> {
                override fun onResponse(
                    call: Call<GetDoctorResponse>,
                    response: Response<GetDoctorResponse>,
                ) {
                    if (response.isSuccessful) {
                        val dataDoctor = response.body()?.data?.data
                        if (dataDoctor != null) {
                            setupRecyclerView(dataDoctor)
                        }
                    }
                }

                override fun onFailure(call: Call<GetDoctorResponse>, t: Throwable) {

                }

            })
    }

    private fun setupRecyclerView(listData: List<DataItemDokter>) {
        val adapter = DoctorAdapter(listData)
        binding.apply {
            rvListDoctor.layoutManager = LinearLayoutManager(requireActivity())
            rvListDoctor.adapter = adapter
            rvListDoctor.setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}