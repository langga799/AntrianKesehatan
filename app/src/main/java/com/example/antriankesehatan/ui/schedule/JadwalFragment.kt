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
import com.example.antriankesehatan.model.GetProfileResponse
import com.example.antriankesehatan.model.HariPraktekItem
import com.example.antriankesehatan.model.JamPraktekItem
import com.example.antriankesehatan.model.SetScheduleAntrianResponse
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.Helper
import com.example.antriankesehatan.utils.SharedPreference
import com.example.antriankesehatan.utils.loadImageView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class JadwalFragment : Fragment() {

    private var _binding: FragmentJadwalBinding? = null
    private val binding get() = _binding
    private lateinit var preference: SharedPreference

    companion object {
        private const val MAX_SELECTABLE_DAY = 365
    }


    private var doctorId = ""
    private var praktekId = ""
    private var inputJam = arrayListOf<String>()
    private var inputWaktu = arrayListOf<String>()


    private var dataHari = ArrayList<String>()
    private var dataJadwal = ArrayList<JamPraktekItem>()


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
            arguments?.getSerializable("DATA_HARI_PRAKTEK_DOCTOR") as ArrayList<HariPraktekItem>
        val bundlePhotoDokter = arguments?.getString("DATA_PHOTO_DOCTOR", "")
        val bundleDataPraktek =
            arguments?.getSerializable("DATA_PRAKTEK") as ArrayList<JamPraktekItem>


        Log.d("+++++++++++++++++++", bundleIDDokter.toString())
        Log.d("+++++++++++++++++++", bundleBidangDokter.toString())
        Log.d("+++++++++++++++++++", bundleNamaDokter.toString())
        Log.d("+++++++++++++++++++", bundleHariPraktekDokter.toString())
        Log.d("+++++++++++++++++++", bundlePhotoDokter.toString())


        for (hari in bundleHariPraktekDokter) {
            dataHari.add(hari.hariPraktek)
        }

        Log.d("Data-Hari", dataHari.toString())


//        val data = arrayListOf<JamPraktekItem>()
//
//        for (praktek in bundleHariPraktekDokter) {
//            data.addAll(praktek.jamPraktek)
//        }
//
//        for (new in data) {
//            Log.d("jampraktek", new.jamPraktek)
//            Log.d("waktupraktek", new.shift)
//
//        }


//        for (praktek in bundleHariPraktekDokter as ArrayList<HariPraktekItem>) {
//            praktekId = praktek.id.toString()
//            doctorId = praktek.dokterId.toString()
//
//            Log.d("HARI-PRAKTEK", praktek.toString())
//
//            dataJadwal = praktek.jamPraktek


        //  inputJam.clear()
        //    inputWaktu.clear()

//            for (jamPraktek in hariPraktek.jampraktek) {
//                inputJam.clear()
//                inputJam.add(jamPraktek.jamPraktek)
//                Log.d("JAM-PRAKTEK", jamPraktek.toString())
//            }

//            for (shiffPraktek in hariPraktek.jampraktek){
//                inputWaktu.clear()
//                inputWaktu.add(shiffPraktek.shift)
//            }


        //      }


//        for (jadwal in dataJadwal){
//            inputJam.add(jadwal.jamPraktek)
//            inputWaktu.add(jadwal.shift)
//        }
//


        binding?.ivDoctor?.let {
            activity?.loadImageView(Helper.BASE_IMAGE_URL_DOKTER + bundlePhotoDokter.toString(),
                it)
        }
        binding?.tvDoctorName?.text = bundleNamaDokter
        binding?.tvDoctorType?.text = bundleBidangDokter


        getProfile()
        inputDate()
        inputTime(inputJam)
        inputShiff(inputWaktu)



        binding?.btnSendRequestNoAntrian?.setOnClickListener {
            setRequestNoAntrian()
        }
    }


    private fun inputDate() {
        binding?.textInputLayoutDate?.setEndIconOnClickListener {

            val date = Calendar.getInstance()
            val dpd: DatePickerDialog =
                DatePickerDialog.newInstance({ view, year, month, day ->
                    val dateSelected = "$day-$month-$year"
                    binding?.edtInputDate?.setText(dateSelected)
                },
                    date[Calendar.YEAR],
                    date[Calendar.MONTH],
                    date[Calendar.DAY_OF_MONTH]
                )


            val activeDays = ArrayList<Calendar>()
            for (i in 0 until MAX_SELECTABLE_DAY) {

                when {
                    dataHari.contains("senin") -> {
                        if (date[Calendar.DAY_OF_WEEK] == Calendar.MONDAY || date[Calendar.DAY_OF_WEEK] == Calendar.MONDAY) {
                            val d = date.clone() as Calendar
                            activeDays.add(d)
                        }
                    }
                }

                when {
                    dataHari.contains("selasa") -> {
                        if (date[Calendar.DAY_OF_WEEK] == Calendar.TUESDAY || date[Calendar.DAY_OF_WEEK] == Calendar.TUESDAY) {
                            val d = date.clone() as Calendar
                            activeDays.add(d)
                        }
                    }
                }

                when {
                    dataHari.contains("rabu") -> {
                        if (date[Calendar.DAY_OF_WEEK] == Calendar.WEDNESDAY || date[Calendar.DAY_OF_WEEK] == Calendar.WEDNESDAY) {
                            val d = date.clone() as Calendar
                            activeDays.add(d)
                        }
                    }
                }

                when {
                    dataHari.contains("kamis") -> {
                        if (date[Calendar.DAY_OF_WEEK] == Calendar.THURSDAY || date[Calendar.DAY_OF_WEEK] == Calendar.THURSDAY) {
                            val d = date.clone() as Calendar
                            activeDays.add(d)
                        }
                    }
                }

                when {
                    dataHari.contains("jumat") -> {
                        if (date[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY || date[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY) {
                            val d = date.clone() as Calendar
                            activeDays.add(d)
                        }
                    }
                }

                when {
                    dataHari.contains("sabtu") -> {
                        if (date[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || date[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY) {
                            val d = date.clone() as Calendar
                            activeDays.add(d)
                        }
                    }
                }

                when {
                    dataHari.contains("minggu") -> {
                        if (date[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY || date[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
                            val d = date.clone() as Calendar
                            activeDays.add(d)
                        }
                    }
                }

                date.add(Calendar.DATE, 1)
            }

            val selectableDay: Array<Calendar> = activeDays.toArray(arrayOfNulls(activeDays.size))
            dpd.selectableDays = selectableDay

            dpd.setOnCancelListener {
                it.dismiss()
            }

            dpd.show(childFragmentManager, "DatePicker")


        }


//            val dateInput = MaterialDatePicker.Builder.datePicker()
//                .setTitleText("Pilih tanggal")
//                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                .build()
//
//            dateInput.apply {
//                addOnNegativeButtonClickListener {
//                    dismiss()
//                }
//                addOnPositiveButtonClickListener {
//                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//                    calendar.timeInMillis = selection!!
//                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                    val formattedDate = format.format(calendar.time)
//
//
//                    val newCalendar = Calendar.getInstance()
//                    val today = newCalendar.timeInMillis
//                    val oneDay = 24 * 60 * 60 * 1000L
//
//
//
//
//                    binding?.edtInputDate?.setText(formattedDate)
//                }
//                show(this@JadwalFragment.requireActivity().supportFragmentManager, "DATE_PICKER")
//
//            }


    }

    private fun inputTime(listJam: ArrayList<String>) {
        Log.d("TIME", listJam.toString())
        //   listJam.removeAt(2)
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_time, listJam)
        (binding?.edtLayoutTime?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun inputShiff(listWaktu: ArrayList<String>) {
        Log.d("WAKTU", listWaktu.toString())
        //   listWaktu.removeAt(2)
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_time, listWaktu)
        (binding?.edtLayoutDayNight?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun inputBpjs(dataBPJS: String) {
        Log.d("bpjS", dataBPJS)
        var listChoose = arrayListOf<String>()

        if (dataBPJS.isNotEmpty()) {
            listChoose.clear()
            listChoose.add(0, dataBPJS)
            listChoose.add(1, "Anda tidak memiliki BPJS")

            val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_bpjs, listChoose)
            (binding?.edtLayoutBpjs?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        } else {
            listChoose = arrayListOf("Menggunakan BPJS", "Tidak Menggunakan BPJS")
            val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_bpjs, listChoose)
            (binding?.edtLayoutBpjs?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }


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

    private fun getProfile() {
        val token = preference.getToken()
        NetworkConfig().getApiService().getProfile("Bearer $token")
            .enqueue(object : Callback<GetProfileResponse> {
                override fun onResponse(
                    call: Call<GetProfileResponse>,
                    response: Response<GetProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data
//                        if (data?.noBpjs == null){
//                            inputBpjs("-")
//                        }
//                        data?.noBpjs?.let { inputBpjs(it) }
                        when {
                            data?.noBpjs != null -> {
                                inputBpjs(data.noBpjs)
                            }
                            else -> {
                                inputBpjs("-")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {

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