package com.drexask.reduplicate.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.withCreated
import androidx.lifecycle.withResumed
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.BackButtonDialogFragment
import com.drexask.reduplicate.DuplicateRemoverFragmentViewModel
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentDuplicateRemoverBinding
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.usecases.ConvertBytesUseCase
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DuplicateRemoverFragment : Fragment() {

    @Inject
    lateinit var convertBytesUseCase: ConvertBytesUseCase

    private val mainViewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)
    private val viewModel: DuplicateRemoverFragmentViewModel by viewModels()

    private var _binding: FragmentDuplicateRemoverBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicateRemoverBinding.inflate(layoutInflater)

        setupObservers()
        setupListeners()

        changeBackButtonBehavior()

        return binding.root
    }

    private fun changeBackButtonBehavior() {
        val onBackPressedDispatcher = activity?.onBackPressedDispatcher
        onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            if (viewModel.areDuplicatesRemoved) {
                showGoBackDialogFragment()
            } else {
                remove()
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showGoBackDialogFragment() {
        val dialogFragment =
            BackButtonDialogFragment()

        CoroutineScope(Dispatchers.Main).launch {
            dialogFragment.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.CustomBottomSheetDialogTheme
            )

            dialogFragment.show(childFragmentManager, null)

            dialogFragment.withResumed {
                dialogFragment.setMessage(getString(R.string.dialog_fragment_back_button_message))
                dialogFragment.setOnPositiveButtonListener {
                    mainViewModel.foundDuplicatesList.clear()
                    findNavController().popBackStack(R.id.folderPickerFragment, false)
                }
                dialogFragment.setOnNegativeButtonListener {
                    dialogFragment.dialog?.cancel()
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.numberOfRemovedFilesLD.observe(viewLifecycleOwner, removedFilesObserver)
        viewModel.numberOfRemovedBytesLD.observe(viewLifecycleOwner, removedBytesObserver)
    }

    private fun setupListeners() {
        clickGoBack()
        clickRemoveDuplicates()
    }

    private fun clickGoBack() {
        binding.btnGoBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun clickRemoveDuplicates() {
        binding.apply {
            btnRemoveDuplicates.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    progressBarUp.visibility = View.VISIBLE
                    progressBarDown.visibility = View.VISIBLE

                    withContext(Dispatchers.Default) {
                        viewModel.collectRemovingProgressFlow()
                        viewModel.removeDuplicates(mainViewModel.foundDuplicatesList)
                    }

                    progressBarUp.visibility = View.GONE
                    progressBarDown.visibility = View.GONE
                }
            }
        }
    }

    private val removedFilesObserver = Observer<Int> {
        binding.tvNumberOfFiles.text = getString(R.string.results_files, it)
    }

    private val removedBytesObserver = Observer<Long> {
        val result = convertBytesUseCase.execute(it)
        val resultString = "${"%.2f".format(result.first)} ${result.second}"
        binding.tvNumberOfBytes.text = getString(R.string.results_bytes, resultString)
    }

}