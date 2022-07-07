package com.example.antriankesehatan.ui.schedule

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentJadwalBinding
import com.example.antriankesehatan.model.HariPraktekItem
import com.example.antriankesehatan.model.SetScheduleAntrianResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.Helper
import com.example.antriankesehatan.utils.SharedPreference
import com.example.antriankesehatan.utils.loadImageView
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class JadwalFragment : Fragment() {

    private var _binding: FragmentJadwalBinding? = null
    private val binding get() = _binding
    private lateinit var preference: SharedPreference


    private var doctorId = ""
    private var praktekId = ""
    private var inputJam = arrayListOf<String>()
    private var inputWaktu = arrayListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentJadwalBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        preference = SharedPreference(requireActivity())


        val bundleIDDokter = arguments?.getString("DATA_ID_DOCTOR", "")
        val bundleBidangDokter = arguments?.getString("DATA_BIDANG_DOCTOR", "")
        val bundleNamaDokter = arguments?.getString("DATA_NAMA_DOCTOR", "")
        val bundleHariPraktekDokter =
            arguments?.getSerializable("DATA_HARI_PRAKTEK_DOCTOR") as ArrayList<*>
        val bundlePhotoDokter = arguments?.getString("DATA_PHOTO_DOCTOR", "")


        Log.d("+++++++++++++++++++", bundleIDDokter.toString())
        Log.d("+++++++++++++++++++", bundleBidangDokter.toString())
        Log.d("+++++++++++++++++++", bundleNamaDokter.toString())
        Log.d("+++++++++++++++++++", bundleHariPraktekDokter.toString())
        Log.d("+++++++++++++++++++", bundlePhotoDokter.toString())


        for (hariPraktek in bundleHariPraktekDokter as ArrayList<HariPraktekItem>) {
            praktekId = hariPraktek.id.toString()
            doctorId = hariPraktek.dokterId.toString()

            Log.d("HARI-PRAKTEK", hariPraktek.toString())

            for (jamPraktek in hariPraktek.jampraktek) {
                inputJam.add(jamPraktek.jamPraktek)
                inputWaktu.add(jamPraktek.shift)

                Log.d("JAM-PRAKTEK", jamPraktek.toString())
            }
        }


        binding?.ivDoctor?.let {
            activity?.loadImageView(Helper.BASE_IMAGE_URL_DOKTER + bundlePhotoDokter.toString(),
                it)
        }
        binding?.tvDoctorName?.text = bundleNamaDokter
        binding?.tvDoctorType?.text = bundleBidangDokter


        inputDate()
        inputTime(inputJam)
        inputShiff(inputWaktu)
        inputBpjs()


        binding?.btnSendRequestNoAntrian?.setOnClickListener {
            setRequestNoAntrian()
        }
    }


    private fun inputDate() {
        binding?.textInputLayoutDate?.setEndIconOnClickListener {
            val dateInput = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih tanggal")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            dateInput.apply {
                addOnNegativeButtonClickListener {
                    dismiss()
                }
                addOnPositiveButtonClickListener {
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    calendar.timeInMillis = selection!!
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = format.format(calendar.time)

                    binding?.edtInputDate?.setText(formattedDate)
                }
                show(this@JadwalFragment.requireActivity().supportFragmentManager, "DATE_PICKER")

            }


        }
    }

    private fun inputTime(listJam: List<String>) {
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_time, listJam)
        (binding?.edtLayoutTime?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun inputShiff(listWaktu: List<String>) {
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_time, listWaktu)
        (binding?.edtLayoutDayNight?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun inputBpjs() {
        val listChoose = listOf("Menggunakan BPJS", "Tidak Menggunakan BPJS")
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_bpjs, listChoose)
        (binding?.edtLayoutBpjs?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }


    private fun setRequestNoAntrian() {
        val token = preference.getToken()

        val inputDate = binding?.edtInputDate?.text.toString()
        val inputShiff = binding?.edtDayNight?.text.toString()
        val inputBPJS = binding?.edtBpjs?.text.toString()
        val inputKeluhan = binding?.edtNotes?.text.toString()

        NetworkConfig().getApiService().setScheduleAntrian(
            token = "Bearer $token",
            doctorId = doctorId,
            jamPraktekId = praktekId,
            shiff = inputShiff,
            tanggalPendaftaran = inputDate,
            transaksi = inputBPJS,
            keluhan = inputKeluhan
        ).enqueue(object : Callback<SetScheduleAntrianResponse> {
            override fun onResponse(
                call: Call<SetScheduleAntrianResponse>,
                response: Response<SetScheduleAntrianResponse>,
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireActivity(), "Success", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_jadwalFragment_to_doctorFragment)
                }
            }

            override fun onFailure(call: Call<SetScheduleAntrianResponse>, t: Throwable) {

            }

        })

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_jadwalFragment_to_doctorFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}