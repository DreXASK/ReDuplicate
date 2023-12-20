package com.drexask.reduplicate.presentation.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Observer
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentDuplicateRemoverBinding


class DuplicateRemoverFragment : Fragment() {

    private val viewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)

    private var _binding: FragmentDuplicateRemoverBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicateRemoverBinding.inflate(layoutInflater)

        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.removedBytes.observe(viewLifecycleOwner, removedBytesObserver)
    }

    private fun setupListeners() {
        clickRemoveDuplicates()
    }

    private fun clickRemoveDuplicates() {
        binding.btnRemoveDuplicates.setOnClickListener {
            viewModel.removeDuplicates()
        }
    }

    private val removedBytesObserver = Observer<Long> {
        println(it)
    }

}