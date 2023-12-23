package com.drexask.reduplicate

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.databinding.BottomSheetDialogSettingsBinding
import com.drexask.reduplicate.databinding.DialogFragmentBackButtonBinding

class BackButtonDialogFragment(
    private val message: String,
    @LayoutRes contentLayoutId: Int
): DialogFragment(contentLayoutId) {

    private var _binding: DialogFragmentBackButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        _binding = view?.let { DialogFragmentBackButtonBinding.bind(it) }

        setupListeners()
        setMessage()

        return view
    }

    private fun setMessage() {
        binding.tvMessage.text = message
    }

    private fun setupListeners() {
        clickPositiveButton()
        clickNegativeButton()
    }

    private fun clickPositiveButton() {
        binding.btnPositive.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun clickNegativeButton() {
        binding.btnNegative.setOnClickListener {
            dialog?.cancel()
        }
    }
}