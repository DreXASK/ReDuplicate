package com.drexask.reduplicate.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentDuplicateRemoverBinding
import com.drexask.reduplicate.domain.usecases.ConvertBytesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DuplicateRemoverFragment : Fragment() {

    @Inject
    lateinit var convertBytesUseCase: ConvertBytesUseCase

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
        viewModel.numberOfRemovedFilesLD.observe(viewLifecycleOwner, removedFilesObserver)
        viewModel.numberOfRemovedBytesLD.observe(viewLifecycleOwner, removedBytesObserver)
    }

    private fun setupListeners() {
        clickGoBack()
        clickRemoveDuplicates()
    }

    private fun clickGoBack() {
        binding.btnGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun clickRemoveDuplicates() {
        binding.apply {
            btnRemoveDuplicates.setOnClickListener {
                btnGoBack.isEnabled = false
                CoroutineScope(Dispatchers.IO).launch {

                    withContext(Dispatchers.Main) {
                        progressBarUp.visibility = View.VISIBLE
                        progressBarDown.visibility = View.VISIBLE
                    }

                    viewModel.collectRemovingProgressFlow()
                    viewModel.removeDuplicates()

                    withContext(Dispatchers.Main) {
                        progressBarUp.visibility = View.GONE
                        progressBarDown.visibility = View.GONE
                    }
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