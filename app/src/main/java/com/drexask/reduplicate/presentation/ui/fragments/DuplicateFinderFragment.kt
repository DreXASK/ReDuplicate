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

            val scannedFolder = viewModel.getScannedFolderOrNull()

            viewModel.resetDuplicatesMap()
            viewModel.fillDuplicatesMap(scannedFolder)

            viewModel.duplicatesMap.value?.map {
                println(it.key)
                it.value.map { storageFile -> println(storageFile.file.uri.path) }
                println("---------------")
            }

        }
    }

    private fun clickOpenSettings() {
        binding.btnSettings.setOnClickListener {
            showSettingsDialog()
        }
    }


    private val treeUriObserver = Observer<Uri> {
        binding.currentFolderName.text = it.lastPathSegment?.replaceFirst("primary:", "")
    }

/*    private suspend fun deleteThisPlease() {
        var currentProgress = 0
        while (currentProgress <= 10000) {
            CoroutineScope(Dispatchers.Main).launch {
                _binding?.progressCircular?.progress = currentProgress
                _binding?.textView?.text = currentProgress.toString()
                currentProgress += 10
            }
            delay(10L)
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}