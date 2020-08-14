package com.example.contactbook.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.contactbook.R
import com.example.contactbook.databinding.FragmentDateAndTimeDialogBinding
import kotlinx.android.synthetic.main.fragment_date_and_time_dialog.*
import java.time.LocalDateTime
import java.util.*

class DateAndTimeDialogFragment(val dataAndTimeListener: (dataTime: LocalDateTime) -> Unit) : DialogFragment() {

    companion object {
        const val TAG = "DateAndTimeDialogFrag"
    }

    private lateinit var binding: FragmentDateAndTimeDialogBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_date_and_time_dialog, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val today = Calendar.getInstance()
        binding.minutesNumberPicker.wrapSelectorWheel = false
        binding.hoursNumberPicker.wrapSelectorWheel = false
        binding.datePicker.minDate = today.timeInMillis
        binding.datePicker.maxDate = today.timeInMillis+31556952000
        binding.hoursNumberPicker.minValue = today.get(Calendar.HOUR_OF_DAY)
        binding.hoursNumberPicker.maxValue = 23
        binding.minutesNumberPicker.minValue = today.get(Calendar.MINUTE)
        binding.minutesNumberPicker.maxValue = 59

        binding.hoursNumberPicker.setOnValueChangedListener(){ view, old, new ->
            if(new == today.get(Calendar.HOUR_OF_DAY)){
                binding.minutesNumberPicker.minValue = today.get(Calendar.MINUTE)
                binding.minutesNumberPicker.maxValue = 59
            }
            else{
                binding.minutesNumberPicker.minValue = 0
                binding.minutesNumberPicker.maxValue = 59
            }
        }

        binding.datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)) { view, year, month, day ->

            if(year == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH) && day == today.get(Calendar.DAY_OF_MONTH)){
                binding.hoursNumberPicker.minValue = today.get(Calendar.HOUR_OF_DAY)
                binding.hoursNumberPicker.maxValue = 23
                binding.minutesNumberPicker.maxValue = 59
                binding.minutesNumberPicker.minValue = today.get(Calendar.MINUTE)
            }
            else{
                binding.hoursNumberPicker.minValue = 0
                binding.hoursNumberPicker.maxValue = 23
                binding.minutesNumberPicker.minValue = 0
                binding.minutesNumberPicker.maxValue = 59
            }
        }

        binding.submitBtn.setOnClickListener {
            val selectedDate = LocalDateTime.of(binding.datePicker.year, binding.datePicker.month+1, binding.datePicker.dayOfMonth, binding.hoursNumberPicker.value, binding.minutesNumberPicker.value)
            dataAndTimeListener(selectedDate)
            Log.i(TAG, selectedDate.toString())
            dismiss()
        }

        return binding.root
    }
}