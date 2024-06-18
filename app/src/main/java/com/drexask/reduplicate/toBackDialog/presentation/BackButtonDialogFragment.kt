package com.drexask.reduplicate.toBackDialog.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.DialogFragmentBackButtonBinding

class BackButtonDialogFragment
    : DialogFragment(R.layout.dialog_fragment_back_button) {

    private var _binding: DialogFragmentBackButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        _binding = view?.let { DialogFragmentBackButtonBinding.bind(it) }

        return view
    }

    fun setMessage(message: String) {
        binding.tvMessage.text = message
    }


    fun setOnPositiveButtonListener(callBack: () -> Unit) {
        binding.btnPositive.setOnClickListener {
            callBack()
        }
    }

    fun setOnNegativeButtonListener(callBack: () -> Unit) {
        binding.btnNegative.setOnClickListener {
            callBack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}