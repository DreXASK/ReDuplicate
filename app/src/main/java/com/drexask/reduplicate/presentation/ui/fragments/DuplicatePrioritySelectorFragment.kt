package com.drexask.reduplicate.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.DuplicateCardsAdapter
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentDuplicatePrioritySelectorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DuplicatePrioritySelectorFragment : Fragment() {

    private val viewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)

    private var _binding: FragmentDuplicatePrioritySelectorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicatePrioritySelectorBinding.inflate(layoutInflater)

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
            val duplicateCardsAdapter = DuplicateCardsAdapter(it, viewModel.foundDuplicatesList!!)

            binding.rvDuplicateCards.apply {
                adapter = duplicateCardsAdapter
                setHasFixedSize(true)
            }
        }
    }
}