package com.example.contactbook.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.contactbook.R
import com.example.contactbook.databinding.FragmentDateAndTimeDialogBinding
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

        binding.datePicker.updateDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        binding.datePicker.minDate = today.timeInMillis
        binding.datePicker.maxDate = today.timeInMillis+31556952000
        binding.timePicker.setIs24HourView(true)
        binding.timePicker.hour = today.get(Calendar.HOUR_OF_DAY)
        binding.timePicker.minute = today.get(Calendar.MINUTE)

        binding.negativeBtn.setOnClickListener {
            Log.i(TAG, binding.timePicker.hour.toString())
            dataAndTimeListener(LocalDateTime.of(binding.datePicker.year, binding.datePicker.month+1, binding.datePicker.dayOfMonth, binding.timePicker.hour, binding.timePicker.minute))
            dismiss()
        }

        return binding.root
    }
}