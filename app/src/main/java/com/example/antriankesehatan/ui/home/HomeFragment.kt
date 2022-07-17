package com.example.antriankesehatan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antriankesehatan.databinding.FragmentHomeBinding
import com.example.antriankesehatan.model.CurrentAntrianResponse
import com.example.antriankesehatan.model.DataItemCurrentAntrian
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.pusher.NotificationPusher
import com.example.antriankesehatan.utils.SharedPreference
import com.example.antriankesehatan.utils.gone
import com.example.antriankesehatan.utils.visible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var preference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireActivity())

      //  NotificationPusher.setUpPusher(requireActivity())
        getAntrianSaatIni()

    }

    private fun getAntrianSaatIni() {
        val token = preference.getToken()

        NetworkConfig().getApiService().getAntrianSaatIni("Bearer $token", "true")
            .enqueue(object : Callback<CurrentAntrianResponse> {
                override fun onResponse(
                    call: Call<CurrentAntrianResponse>,
                    response: Response<CurrentAntrianResponse>,
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.data
                            if (!data.isNullOrEmpty()){
                                setupRecyclerView(data)
                                binding?.warningAntrian?.gone()
                            } else {
                                binding?.warningAntrian?.visible()
                            }
                        }
                        401 -> {
                            Toast.makeText(requireActivity(),
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            Toast.makeText(requireActivity(),
                                "Something Wrong",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                override fun onFailure(call: Call<CurrentAntrianResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun setupRecyclerView(data: List<DataItemCurrentAntrian?>) {
        val adapter = CurrentAntrianAdapter(data)
        binding?.apply {
            rvCurrentAntrian.adapter = adapter
            rvCurrentAntrian.layoutManager = LinearLayoutManager(requireActivity())
            rvCurrentAntrian.setHasFixedSize(true)

            if (data.isEmpty() ) {
                binding?.warningAntrian?.visible()
            } else {
                binding?.warningAntrian?.gone()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}