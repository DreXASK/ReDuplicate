package com.drexask.reduplicate.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Observer
import com.drexask.reduplicate.DuplicateFinderFragmentViewModel
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.BottomSheetDialogSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsBottomSheetDialogFragment(@LayoutRes layoutRes: Int): BottomSheetDialogFragment(layoutRes) {

    private val viewModel: DuplicateFinderFragmentViewModel by activityViewModels()

    private var _binding: BottomSheetDialogSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        _binding = view?.let { BottomSheetDialogSettingsBinding.bind(it) }

        setupListeners()
        setupObservers()

        return view
    }

    private fun setupListeners() {
        clickFileNames()
        clickFileWeights()
    }

    private fun clickFileNames() {
        binding.chipFileNames.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useFileNamesLD.value = isChecked
        }
    }
    private fun clickFileWeights() {
        binding.chipFileWeights.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useFileWeightsLD.value = isChecked
        }
    }

    private fun setupObservers() {
        viewModel.useFileNamesLD.observe(this, useFileNamesObserver)
        viewModel.useFileWeightsLD.observe(this, useFileWeightsObserver)
    }

    private val useFileNamesObserver = Observer<Boolean> {
        binding.chipFileNames.isChecked = it
    }

    private val useFileWeightsObserver = Observer<Boolean> {
        binding.chipFileWeights.isChecked = it
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}