package com.drexask.reduplicate.presentation.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentDuplicateFinderBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DuplicateFinderFragment : Fragment() {

    private val viewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)

    private var _binding: FragmentDuplicateFinderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicateFinderBinding.inflate(layoutInflater)

        viewModel.folderFileDoc.value = context?.let { context ->
            viewModel.treeUri.value?.let {treeUri ->
                DocumentFile.fromTreeUri(context, treeUri)
            }
        }

        setupListeners()
        viewModel.treeUri.observe(viewLifecycleOwner, treeUriObserver)
        viewModel.numberOfProcessedFiles.observe(viewLifecycleOwner, numberOfProcessedFilesObserver)


        return binding.root
    }

    private fun showSettingsDialog() {
        val dialog = SettingsBottomSheetDialogFragment(R.layout.bottom_sheet_dialog_settings)
        dialog.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        dialog.show(childFragmentManager, null)
    }

    private fun setupListeners() {
        clickBackToFolderPicker()
        clickLaunch()
        clickOpenSettings()
    }

    private fun clickBackToFolderPicker() {
        binding.btnBackToFolderPicker.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun clickLaunch() {
        binding.btnLaunch.setOnClickListener {

            if (viewModel.useFileNames.value == false
                && viewModel.useFileWeights.value == false
                && viewModel.useFileHashes.value == false) {
                showSettingsDialog()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {

                viewModel.numberOfProcessedFiles.postValue(0)
                viewModel.scanFolder()

                binding.progressCircular.max = viewModel.getItemsQuantityInSelectedFolderAndRememberIt()

                println("Говорит FINDERFRAGMENT: " + Thread.currentThread())
                viewModel.getDuplicates()

/*                viewModel.duplicates?.duplicatesMap?.map {
                    println(it.key)
                    it.value.map { storageFile -> println(storageFile.file.uri.path) }
                    println("---------------")
                }*/
            }
        }
    }

    private fun clickOpenSettings() {
        binding.btnSettings.setOnClickListener {
            showSettingsDialog()
        }
    }


    private val treeUriObserver = Observer<Uri> {
        binding.tvCurrentFolderName.text = it.lastPathSegment?.replaceFirst("primary:", "")
    }

    private val numberOfProcessedFilesObserver = Observer<Int> {
        binding.progressCircular.progress = it
        val finalQuantity = binding.progressCircular.max
        binding.tvCurrentProgress.text = getString(R.string.current_progress, it, finalQuantity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}