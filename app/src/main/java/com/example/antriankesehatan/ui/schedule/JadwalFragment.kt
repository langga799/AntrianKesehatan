package com.example.antriankesehatan.ui.schedule

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentJadwalBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class JadwalFragment : Fragment() {

    private var _binding: FragmentJadwalBinding? = null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentJadwalBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        inputDate()
        inputTime()
        inputBpjs()


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
                    val format = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                    val formattedDate = format.format(calendar.time)

                    binding?.edtInputDate?.setText(formattedDate)
                }
                show(this@JadwalFragment.requireActivity().supportFragmentManager, "DATE_PICKER")

            }


        }
    }

    private fun inputTime(){
        val listTime = listOf("Pagi - Jam 06:00-07:00", "Sore - Jam 17:00-22:00")
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_time, listTime)
        (binding?.edtLayoutTime?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun inputBpjs(){
        val listChoose = listOf("Menggunakan BPJS", "Tidak Menggunakan BPJS")
        val adapter = ArrayAdapter(requireActivity(), R.layout.list_item_bpjs, listChoose)
        (binding?.edtLayoutBpjs?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                findNavController().navigate(R.id.action_jadwalFragment_to_doctorFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}