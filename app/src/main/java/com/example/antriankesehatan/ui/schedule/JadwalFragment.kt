package com.example.antriankesehatan.ui.schedule

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
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
import com.jakewharton.rxbinding2.widget.RxTextView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.reactivex.Observable
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
        binding?.btnSendRequestNoAntrian?.isEnabled = false

        preference = SharedPreference(requireActivity())


        val bundleIDDokter = arguments?.getString("DATA_ID_DOCTOR", "") ?: ""
        val bundlePraktekID = arguments?.getString("DATA_PRAKTEK_ID", "") ?: ""
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





        binding?.ivDoctor?.let {
            activity?.loadImageView(Helper.BASE_IMAGE_URL_DOKTER + bundlePhotoDokter.toString(),
                it)
        }
        binding?.tvDoctorName?.text = bundleNamaDokter
        binding?.tvDoctorType?.text = bundleBidangDokter


        doctorId = bundleIDDokter
        praktekId = bundlePraktekID

        for (hari in bundleHariPraktekDokter) {
            hari.dokterId
            dataHari.add(hari.hariPraktek)
        }
        Log.d("Data-Hari", dataHari.toString())


        inputDate()


        for (jam in bundleDataPraktek) {
            inputJam.add(jam.jamPraktek)
        }

        for (shiff in bundleDataPraktek) {
            inputWaktu.add(shiff.shift)
        }



        inputShiff(inputWaktu)

        getProfile()





        streamValidation()

    }


    private fun inputDate() {
        binding?.textInputLayoutDate?.setEndIconOnClickListener {

            val date = Calendar.getInstance()
            val dpd: DatePickerDialog =
                DatePickerDialog.newInstance({ _, year, month, day ->
                    val dateSelected = "$year-$month-$day"
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
    }


    private fun inputShiff(listShiff: ArrayList<String>) {
        Log.d("SHIFF", listShiff.toString())
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_time, listShiff.distinct())
        (binding?.edtLayoutDayNight?.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        val newListJam = inputJam.distinct()
        val listener = binding?.edtLayoutDayNight?.editText as? AutoCompleteTextView

        listener?.setOnItemClickListener { _, _, _, _ ->
            when (newListJam.size) {
                1 -> {
                    when (listener.text.toString()) {
                        "pagi" -> {
                            val newJam = newListJam[0]
                            Log.d("newjam", newJam)
                            val newList = arrayListOf(newJam)
                            val adapterJam =
                                ArrayAdapter(requireActivity(), R.layout.list_item_time, newList)
                            (binding?.edtLayoutTime?.editText as? AutoCompleteTextView)?.setAdapter(
                                adapterJam)
                            binding?.edtTime?.setText(newJam)
                        }
                        "malam" -> {
                            val newJam = newListJam[0]
                            val newList = arrayListOf(newJam)
                            val adapterJam =
                                ArrayAdapter(requireActivity(), R.layout.list_item_time, newList)
                            (binding?.edtLayoutTime?.editText as? AutoCompleteTextView)?.setAdapter(
                                adapterJam)
                            binding?.edtTime?.setText(newJam)
                        }
                    }
                }
                2 -> {
                    when (listener.text.toString()) {
                        "pagi" -> {
                            val newJam = newListJam[0]
                            val newList = arrayListOf(newJam)
                            val adapterJam =
                                ArrayAdapter(requireActivity(), R.layout.list_item_time, newList)
                            (binding?.edtLayoutTime?.editText as? AutoCompleteTextView)?.setAdapter(
                                adapterJam)
                            binding?.edtTime?.setText(newJam)
                        }
                        "malam" -> {
                            val newJam = newListJam[1]
                            val newList = arrayListOf(newJam)
                            val adapterJam =
                                ArrayAdapter(requireActivity(), R.layout.list_item_time, newList)
                            (binding?.edtLayoutTime?.editText as? AutoCompleteTextView)?.setAdapter(
                                adapterJam)
                            binding?.edtTime?.setText(newJam)
                        }
                    }
                }
            }


        }


    }


    private fun inputBpjs(dataBPJS: String) {
        Log.d("bpjS", dataBPJS)
        val listChoose = arrayListOf<String>()

        if (dataBPJS.isNotEmpty()) {
            listChoose.clear()
            listChoose.add(0, dataBPJS)
            listChoose.add(1, "Tidak Menggunakan BPJS")

            val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_bpjs, listChoose)
            (binding?.edtLayoutBpjs?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }

    }


    // Stream Validation
    private fun streamValidation(){
        val dateStream = binding?.edtInputDate?.let {
            RxTextView.textChanges(it)
                .skipInitialValue()
                .map { date ->
                    date.isEmpty()
                }
        }
        dateStream?.subscribe {
            dateValidation(it)
        }

        val shiffStream = binding?.edtDayNight?.let {
            RxTextView.textChanges(it)
                .skipInitialValue()
                .map { shiff ->
                    shiff.isEmpty()
                }
        }
        shiffStream?.subscribe {
            shiffValidation(it)
        }

        val timeStream = binding?.edtTime?.let {
            RxTextView.textChanges(it)
                .skipInitialValue()
                .map { time ->
                    time.isEmpty()
                }
        }
        timeStream?.subscribe {
            timeValidation(it)
        }

        val keluhanStream = binding?.edtBpjs?.let {
            RxTextView.textChanges(it)
                .skipInitialValue()
                .map { keluhan ->
                    keluhan.isEmpty()
                }
        }
        keluhanStream?.subscribe {
            keluhanValidation(it)
        }

        val paymentStream = binding?.edtBpjs?.let {
            RxTextView.textChanges(it)
                .skipInitialValue()
                .map { bpjs ->
                    bpjs.isEmpty()
                }
        }
        paymentStream?.subscribe{
            paymentValidation(it)
        }

        val validationField = Observable.combineLatest(
            dateStream,
            shiffStream,
            timeStream,
            keluhanStream,
            paymentStream
        ){ textDate, textShiff, textTime, textKeluhan, textPayment ->
            !textDate && !textShiff && !textTime && !textKeluhan && !textPayment
        }
        validationField.subscribe{ isValidValidation ->
            if (isValidValidation){
                binding?.btnSendRequestNoAntrian?.isEnabled = true
                binding?.btnSendRequestNoAntrian?.setBackgroundColor(ContextCompat.getColor(requireActivity(),
                    R.color.cyan_500))

                binding?.btnSendRequestNoAntrian?.setOnClickListener {
                    setRequestNoAntrian()
                }

            } else {
                binding?.btnSendRequestNoAntrian?.isEnabled = false
                binding?.btnSendRequestNoAntrian?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            }

        }
    }

    // Validation Field
    private fun dateValidation(isValid: Boolean) {
        binding?.textInputLayoutDate?.error =
            if (isValid) "Tanggal tidak boleh kosong" else null
    }

    private fun shiffValidation(isValid: Boolean) {
        binding?.edtLayoutDayNight?.error =
            if (isValid) "Kolom waktu tidak boleh kosong" else null
    }

    private fun timeValidation(isValid: Boolean) {
        binding?.edtLayoutTime?.error = if (isValid) "Kolom harus dipilih" else null
    }

    private fun keluhanValidation(isValid: Boolean) {
        binding?.edtLayoutNotes?.error = if (isValid) "Kolom keluhan harus diisi" else null
    }

    private fun paymentValidation(isValid: Boolean) {
        binding?.edtLayoutBpjs?.error = if (isValid) "Kolom bpjs harus diisi" else null
    }


    private fun setRequestNoAntrian() {
        val token = preference.getToken()

        val inputDate = binding?.edtInputDate?.text.toString()
        val inputShiff = binding?.edtDayNight?.text.toString()
        val inputTime = binding?.edtTime?.text.toString()
        val inputKeluhan = binding?.edtNotes?.text.toString()
        val inputBPJS = binding?.edtBpjs?.text.toString()


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
                when (response.code()) {
                    200 -> {
                        val popup =
                            SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                        popup.apply {
                            titleText = "SUCCESS"
                            contentText = response.body()?.meta?.message
                            setCancelable(false)
                            setConfirmButton("OK") {
                                findNavController().navigate(R.id.action_jadwalFragment_to_doctorFragment)
                                dismiss()
                            }
                        }.show()

//                        Toast.makeText(requireActivity(),
//                            response.body()?.meta?.message,
//                            Toast.LENGTH_SHORT).show()

                    }
                    401 -> {

                        val popup =
                            SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        popup.apply {
                            titleText = "ERROR"
                            contentText = response.body()?.meta?.message.toString()
                            setCancelable(false)
                            setConfirmButton("OK") {
                                dismiss()
                            }
                        }.show()

//                        Toast.makeText(requireActivity(),
//                            response.body()?.meta?.message,
//                            Toast.LENGTH_SHORT).show()
                    }
                    500 -> {

                        val popup =
                            SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        popup.apply {
                            titleText = "ERROR"
                            contentText = response.body()?.meta?.message.toString()
                            setCancelable(false)
                            setConfirmButton("OK") {
                                dismiss()
                            }
                        }.show()

//                        Toast.makeText(requireActivity(),
//                            response.body()?.meta?.message,
//                            Toast.LENGTH_SHORT).show()
                    }
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