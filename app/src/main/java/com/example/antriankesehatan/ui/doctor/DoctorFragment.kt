package com.example.antriankesehatan.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antriankesehatan.databinding.FragmentDoctorBinding
import com.example.antriankesehatan.model.DataItemDokter
import com.example.antriankesehatan.model.GetDoctorResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.SharedPreference
import com.example.antriankesehatan.utils.gone
import com.example.antriankesehatan.utils.visible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorFragment : Fragment() {

    private var _binding: FragmentDoctorBinding? = null
    private val binding get() = _binding

    private lateinit var preference: SharedPreference
    private var listDataSearch: ArrayList<DataItemDokter> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDoctorBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = SharedPreference(requireActivity())


        getListDoctor()
        searchDoctor()
    }

    private fun searchDoctor() {

        binding?.searchDoctor?.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    requestSearchParameter(query)
                    binding?.searchDoctor!!.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    listDataSearch.clear()
                    getListDoctor()
                }
                return true
            }

        })


    }


    private fun getListDoctor() {
        val token = preference.getToken()

        binding?.loading?.visible()
        binding?.rvListDoctor?.gone()

        NetworkConfig().getApiService().getListDoctor("Bearer $token")
            .enqueue(object : Callback<GetDoctorResponse> {
                override fun onResponse(
                    call: Call<GetDoctorResponse>,
                    response: Response<GetDoctorResponse>,
                ) {
                    when (response.code()) {
                        200 -> {
                            binding?.loading?.gone()
                            binding?.rvListDoctor?.visible()
                            val dataDoctor = response.body()?.data?.data
                            if (!dataDoctor.isNullOrEmpty()) {
                                setupRecyclerView(dataDoctor)
                            } else {
                                return
                            }
                        }
                        401 -> {
                            binding?.loading?.gone()
                            Toast.makeText(requireActivity(),
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            binding?.loading?.gone()
                            Toast.makeText(requireActivity(),
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                override fun onFailure(call: Call<GetDoctorResponse>, t: Throwable) {
                    binding?.loading?.gone()
                    try {
                        Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            })
    }


    private fun requestSearchParameter(query: String) {
        val token = preference.getToken()
        binding?.loading?.visible()
        NetworkConfig().getApiService().searchDataDokter("Bearer $token", query)
            .enqueue(object : Callback<GetDoctorResponse> {
                override fun onResponse(
                    call: Call<GetDoctorResponse>,
                    response: Response<GetDoctorResponse>,
                ) {

                    when (response.code()) {
                        200 -> {
                            binding?.loading?.gone()
                            val data = response.body()?.data?.data!!
                            listDataSearch = data as ArrayList<DataItemDokter>
                            setupRecyclerView(listDataSearch)
                        }
                        401 -> {
                            binding?.loading?.gone()
                            Toast.makeText(requireActivity(),
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            binding?.loading?.gone()
                            Toast.makeText(requireActivity(),
                                response.body()?.meta?.message,
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GetDoctorResponse>, t: Throwable) {
                    binding?.loading?.gone()
                    Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun setupRecyclerView(listData: List<DataItemDokter>) {
        val adapter = DoctorAdapter(listData)
        binding?.apply {
            rvListDoctor.layoutManager = LinearLayoutManager(requireActivity())
            rvListDoctor.adapter = adapter
            rvListDoctor.setHasFixedSize(true)

            if (adapter.itemCount == 0) {
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