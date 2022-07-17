package com.example.antriankesehatan.ui.antrian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentAntrianBinding
import com.example.antriankesehatan.model.DataItemAntrian
import com.example.antriankesehatan.model.GetAntrianResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.SharedPreference
import com.example.antriankesehatan.utils.gone
import com.example.antriankesehatan.utils.visible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AntrianFragment : Fragment() {

    private var _binding: FragmentAntrianBinding? = null
    private val binding get() = _binding!!

    private lateinit var preference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAntrianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        preference = SharedPreference(requireActivity())

        requestNoAntrian()

    }

    private fun requestNoAntrian() {
        val token = preference.getToken()


        NetworkConfig().getApiService().getNoAntrian("Bearer $token")
            .enqueue(object : Callback<GetAntrianResponse> {
                override fun onResponse(
                    call: Call<GetAntrianResponse>,
                    response: Response<GetAntrianResponse>,
                ) {
                    when(response.code()) {
                        200 -> {
                            val data = response.body()?.data!!
                            setupRecyclerView(data)
                        }
                        401 -> {
                            Toast.makeText(requireActivity(), response.body()?.meta?.message, Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            Toast.makeText(requireActivity(), response.body()?.meta?.message, Toast.LENGTH_SHORT).show()
                        }
                    }


                }

                override fun onFailure(call: Call<GetAntrianResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun setupRecyclerView(dataAntrian: List<DataItemAntrian>) {
        val adapter = AntrianAdapter(dataAntrian)
        binding.apply {
            rvNoAntrian.adapter = adapter
            rvNoAntrian.layoutManager = LinearLayoutManager(requireActivity())
            rvNoAntrian.setHasFixedSize(true)

            if (adapter.itemCount == 0){
                binding.warningNomorAntrian.visible()
            } else {
                binding.warningNomorAntrian.gone()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}