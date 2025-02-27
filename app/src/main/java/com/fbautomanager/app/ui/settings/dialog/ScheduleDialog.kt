package com.fbautomanager.app.ui.settings.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fbautomanager.app.R
import com.fbautomanager.app.databinding.DialogScheduleBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleDialog(
    private val currentSettings: Map<String, Any>,
    private val onSave: (Map<String, Any>) -> Unit
) : DialogFragment() {

    private var _binding: DialogScheduleBinding? = null
    private val binding get() = _binding!!
    
    private val calendar = Calendar.getInstance()
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Thiết lập giá trị ban đầu từ cài đặt hiện tại
        setupInitialValues()
        
        // Thiết lập các listener
        setupListeners()
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
        return dialog
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupInitialValues() {
        // Checkbox Enable Schedule
        val scheduleEnabled = currentSettings["schedule_enabled"] as? Boolean ?: false
        binding.cbEnableSchedule.isChecked = scheduleEnabled
        
        // Start Time
        val startTime = currentSettings["start_time"] as? String ?: "08:00"
        binding.btnStartTime.text = startTime
        
        // End Time
        val endTime = currentSettings["end_time"] as? String ?: "20:00"
        binding.btnEndTime.text = endTime
        
        // Interval
        val intervalIndex = currentSettings["interval_index"] as? Int ?: 0
        binding.spinnerInterval.setSelection(intervalIndex)
        
        // Days of Week
        binding.cbMonday.isChecked = currentSettings["monday"] as? Boolean ?: false
        binding.cbTuesday.isChecked = currentSettings["tuesday"] as? Boolean ?: false
        binding.cbWednesday.isChecked = currentSettings["wednesday"] as? Boolean ?: false
        binding.cbThursday.isChecked = currentSettings["thursday"] as? Boolean ?: false
        binding.cbFriday.isChecked = currentSettings["friday"] as? Boolean ?: false
        binding.cbSaturday.isChecked = currentSettings["saturday"] as? Boolean ?: false
        binding.cbSunday.isChecked = currentSettings["sunday"] as? Boolean ?: false
    }
    
    private fun setupListeners() {
        // Button Start Time
        binding.btnStartTime.setOnClickListener {
            showTimePickerDialog(binding.btnStartTime.text.toString()) { time ->
                binding.btnStartTime.text = time
            }
        }
        
        // Button End Time
        binding.btnEndTime.setOnClickListener {
            showTimePickerDialog(binding.btnEndTime.text.toString()) { time ->
                binding.btnEndTime.text = time
            }
        }
        
        // Button Cancel
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        // Button Save
        binding.btnSave.setOnClickListener {
            saveSettings()
        }
    }
    
    private fun showTimePickerDialog(currentTime: String, onTimeSelected: (String) -> Unit) {
        val timeParts = currentTime.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        
        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(time)
            },
            hour,
            minute,
            true
        ).show()
    }
    
    private fun saveSettings() {
        val settings = mutableMapOf<String, Any>()
        
        // Schedule Enabled
        settings["schedule_enabled"] = binding.cbEnableSchedule.isChecked
        
        // Start Time
        settings["start_time"] = binding.btnStartTime.text.toString()
        
        // End Time
        settings["end_time"] = binding.btnEndTime.text.toString()
        
        // Interval
        settings["interval_index"] = binding.spinnerInterval.selectedItemPosition
        
        // Days of Week
        settings["monday"] = binding.cbMonday.isChecked
        settings["tuesday"] = binding.cbTuesday.isChecked
        settings["wednesday"] = binding.cbWednesday.isChecked
        settings["thursday"] = binding.cbThursday.isChecked
        settings["friday"] = binding.cbFriday.isChecked
        settings["saturday"] = binding.cbSaturday.isChecked
        settings["sunday"] = binding.cbSunday.isChecked
        
        onSave(settings)
        dismiss()
    }
} 