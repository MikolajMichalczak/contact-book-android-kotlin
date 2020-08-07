package com.example.contactbook.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.contactbook.R
import com.example.contactbook.databinding.FragmentDateAndTimeDialogBinding
import java.util.*

class DateAndTimeDialogFragment(val providedDay: Int, val providedMonth: Int, val providedYear: Int, val dataAndTimeListener: (day: Int, month: Int, year: Int) -> Unit) : DialogFragment() {

    companion object {
        const val TAG = "DateAndTimeDialogFragment"
    }

    var day: Int = 0
    var month: Int = 0
    var year: Int = 0

    private lateinit var binding: FragmentDateAndTimeDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_date_and_time_dialog, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val today = Calendar.getInstance()

        if(providedDay == 0) {
            binding.datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)) { view, pickerYear, pickerMonth, pickerDay ->
                month = pickerMonth + 1
                year = pickerYear
                day = pickerDay
            }
        }
        else{
            binding.datePicker.init(providedYear, providedMonth, providedDay) { view, pickerYear, pickerMonth, pickerDay ->
                month = pickerMonth + 1
                year = pickerYear
                day = pickerDay
            }
        }

        binding.negativeBtn.setOnClickListener {
            dataAndTimeListener(day, month, year)
            dismiss()
        }

        return binding.root
    }
}