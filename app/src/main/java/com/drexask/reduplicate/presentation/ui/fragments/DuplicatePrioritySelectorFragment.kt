package com.drexask.reduplicate.presentation.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.DuplicateCardsAdapter
import com.drexask.reduplicate.DuplicateFinderFragmentViewModel
import com.drexask.reduplicate.DuplicatePrioritySelectorViewModel
import com.drexask.reduplicate.FOUND_DUPLICATE_LIST
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.TREE_URI
import com.drexask.reduplicate.databinding.FragmentDuplicatePrioritySelectorBinding
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DuplicatePrioritySelectorFragment : Fragment() {
    private val mainViewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)
    private val viewModel: DuplicatePrioritySelectorViewModel by activityViewModels()

    private var _binding: FragmentDuplicatePrioritySelectorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicatePrioritySelectorBinding.inflate(layoutInflater)

        viewModel.getURIsPrioritySet(mainViewModel.foundDuplicatesList!!)

        setupRecyclerView()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        clickGoToFoldersPrioritySettings()
        clickApplyPrioritySettings()
        clickGoToDuplicateRemover()
    }

    private fun clickGoToFoldersPrioritySettings() {
        binding.btnGoToPrioritySettings.setOnClickListener {
            findNavController().navigate(R.id.action_duplicatePrioritySelectorFragment_to_folderPrioritySettingsFragment)
        }
    }

    private fun clickApplyPrioritySettings() {
        binding.btnApplyPrioritySettings.setOnClickListener {
            viewModel.setDuplicatesHighlightedLinesByPriorityList(mainViewModel.foundDuplicatesList!!)
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
            val duplicateCardsAdapter = DuplicateCardsAdapter(it, mainViewModel.foundDuplicatesList!!)

            binding.rvDuplicateCards.apply {
                adapter = duplicateCardsAdapter
                setHasFixedSize(true)
            }
        }
    }
}