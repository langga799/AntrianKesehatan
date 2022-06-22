package com.example.antriankesehatan.ui.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentRiwayatBinding
import com.example.antriankesehatan.model.DataItemRiwayat
import com.example.antriankesehatan.model.RiwayatResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatFragment : Fragment() {

    private lateinit var binding: FragmentRiwayatBinding
    private lateinit var preference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRiwayatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        preference = SharedPreference(requireActivity())

        getHistory()
    }

    private fun getHistory() {
        val token = preference.getToken()
        NetworkConfig().getApiService().getHistoryPatient("Bearer $token")
            .enqueue(object : Callback<RiwayatResponse> {
                override fun onResponse(
                    call: Call<RiwayatResponse>,
                    response: Response<RiwayatResponse>,
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data
                        if (data != null) setupRecyclerView(data)
                    }
                }

                override fun onFailure(call: Call<RiwayatResponse>, t: Throwable) {

                }

            })
    }

    private fun setupRecyclerView(list: List<DataItemRiwayat>) {
        val adapter = RiwayatAdapter(list)
        binding.apply {
            rvRiwayat.adapter = adapter
            rvRiwayat.layoutManager = LinearLayoutManager(requireActivity())
            rvRiwayat.setHasFixedSize(true)
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }


}