package com.tsfb.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tsfb.app.R
import com.tsfb.app.databinding.FragmentSettingsBinding
import com.tsfb.app.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            binding.apply {
                interactionSpeedSlider.value = settings.interactionSpeed.toFloat()
                autoLikeSwitch.isChecked = settings.autoLikeEnabled
                autoCommentSwitch.isChecked = settings.autoCommentEnabled
                autoShareSwitch.isChecked = settings.autoShareEnabled
                commentTemplatesInput.setText(settings.commentTemplates.joinToString("\n"))
            }
        }

        binding.saveButton.setOnClickListener {
            val commentTemplates = binding.commentTemplatesInput.text.toString()
                .split("\n")
                .filter { it.isNotBlank() }

            viewModel.saveSettings(
                interactionSpeed = binding.interactionSpeedSlider.value.toInt(),
                autoLikeEnabled = binding.autoLikeSwitch.isChecked,
                autoCommentEnabled = binding.autoCommentSwitch.isChecked,
                autoShareEnabled = binding.autoShareSwitch.isChecked,
                commentTemplates = commentTemplates
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 