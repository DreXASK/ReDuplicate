package com.drexask.reduplicate.duplicatePrioritySelector.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.withResumed
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentDuplicatePrioritySelectorBinding
import com.drexask.reduplicate.toBackDialog.presentation.BackButtonDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DuplicatePrioritySelectorFragment : Fragment() {

    private val viewModel: DuplicatePrioritySelectorViewModel by viewModels()

    private var _binding: FragmentDuplicatePrioritySelectorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicatePrioritySelectorBinding.inflate(layoutInflater)

        changeBackButtonBehavior()
        setupRecyclerView()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        clickGoBackToDuplicatesFinder()
        clickGoToFoldersPrioritySettings()
        clickApplyPrioritySettings()
        clickGoToDuplicateRemover()
    }

    private fun clickGoBackToDuplicatesFinder() {
        binding.btnBackToDuplicateFinder.setOnClickListener {
            showGoBackDialogFragment()
        }
    }

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
            R.style.CustomDialogFragmentTheme
        )
        dialogFragment.show(childFragmentManager, null)

        CoroutineScope(Dispatchers.Main).launch {
            dialogFragment.withResumed {
                dialogFragment.setMessage(getString(R.string.fragment_duplicate_priority_selector_dialog_fragment_back_button_message))
                dialogFragment.setOnPositiveButtonListener {
                    viewModel.mainActivitySharedData.clearAllData()
                    findNavController().popBackStack()
                }
                dialogFragment.setOnNegativeButtonListener {
                    dialogFragment.dialog?.cancel()
                }
            }
        }
    }

    private fun clickGoToFoldersPrioritySettings() {
        binding.btnGoToPrioritySettings.setOnClickListener {
            findNavController().navigate(R.id.action_duplicatePrioritySelectorFragment_to_folderPrioritySettingsFragment)
        }
    }

    private fun clickApplyPrioritySettings() {
        binding.btnApplyPrioritySettings.setOnClickListener {
            viewModel.setDuplicatesHighlightedLinesByPriorityList()
            binding.rvDuplicateCards.adapter?.notifyDataSetChanged() // TODO("Need to change this to DiffUtil")
        }
    }

    private fun clickGoToDuplicateRemover() {
        binding.btnGoToDuplicateRemover.setOnClickListener {
            findNavController().navigate(R.id.action_duplicatePrioritySelectorFragment_to_duplicateRemoverFragment)
        }
    }

    private fun setupRecyclerView() {
        context?.let {
            val duplicateCardsAdapter = DuplicateCardsAdapter(it, viewModel.mainActivitySharedData.foundDuplicatesList!!)

            binding.rvDuplicateCards.apply {
                adapter = duplicateCardsAdapter
                setHasFixedSize(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}