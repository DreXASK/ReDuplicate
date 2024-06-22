package com.drexask.reduplicate.duplicateRemover.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.withResumed
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentDuplicateRemoverBinding
import com.drexask.reduplicate.toBackDialog.presentation.BackButtonDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DuplicateRemoverFragment : Fragment() {

    @Inject
    lateinit var convertBytes: ConvertBytes

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

        //changeBackButtonBehavior()

        return binding.root
    }

//    private fun changeBackButtonBehavior() {
//        val onBackPressedDispatcher = activity?.onBackPressedDispatcher
//        onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
//            if (viewModel.areDuplicatesRemoved) {
//                showGoBackDialogFragment()
//            } else {
//                remove()
//                onBackPressedDispatcher.onBackPressed()
//            }
//        }
//    }

    private fun changeBackButtonBehavior() {
        val onBackPressedDispatcher = activity?.onBackPressedDispatcher
        onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            showGoBackDialogFragment()
        }
    }

    private fun showGoBackDialogFragment() {
        val dialogFragment = BackButtonDialogFragment()
        dialogFragment.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.CustomBottomSheetDialogTheme
        )
        dialogFragment.show(childFragmentManager, null)

        CoroutineScope(Dispatchers.Main).launch {
            dialogFragment.withResumed {
                dialogFragment.setMessage(getString(R.string.fragment_duplicate_remover_dialog_fragment_back_button_message))
                dialogFragment.setOnPositiveButtonListener {
                    viewModel.mainActivitySharedData.clearAllData()
                    findNavController().popBackStack(R.id.folderPickerFragment, false)
                }
                dialogFragment.setOnNegativeButtonListener {
                    dialogFragment.dialog?.cancel()
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.numberOfRemovedFilesAndBytesLD.observe(
            viewLifecycleOwner,
            removedFilesAndBytesObserver
        )
        viewModel.areDuplicatesBeingRemovedLD.observe(
            viewLifecycleOwner,
            removingStateObserver
        )
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
                viewModel.collectRemovingProgressFlow()
                viewModel.removeDuplicates(viewModel.mainActivitySharedData.foundDuplicatesList!!)

                btnRemoveDuplicates.isEnabled = false
                changeBackButtonBehavior()
            }
        }
    }

    private val removedFilesAndBytesObserver = Observer<Pair<Int, Long>> {
        val bytesResult = convertBytes.execute(it.second)
        val bytesResultString = "${"%.2f".format(bytesResult.first)} ${bytesResult.second}"
        binding.tvResult.text = getString(R.string.results_files, it.first, bytesResultString)
    }

    private val removingStateObserver = Observer<Boolean> {
        when (it) {
            true -> {
                binding.progressBarUp.visibility = View.VISIBLE
                binding.progressBarDown.visibility = View.VISIBLE
            }

            false -> {
                binding.progressBarUp.visibility = View.GONE
                binding.progressBarDown.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}