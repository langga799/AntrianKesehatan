package com.example.antriankesehatan.ui.schedule

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
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


        binding?.btnBackToFragmentDoctor?.setOnClickListener {
            findNavController().apply {
                navigate(R.id.action_jadwalFragment_to_doctorFragment)
            }
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_jadwalFragment_to_doctorFragment)
        }
        callback.isEnabled = true

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
                show(parentFragmentManager, "Fragment Dialog Date")

            }


        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }


}