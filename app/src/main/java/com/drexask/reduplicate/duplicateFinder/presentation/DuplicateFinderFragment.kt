package com.drexask.reduplicate.duplicateFinder.presentation

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.R
import com.drexask.reduplicate.duplicateFinder.utils.TAB
import com.drexask.reduplicate.duplicateFinder.utils.TREE_URI
import com.drexask.reduplicate.databinding.FragmentDuplicateFinderBinding
import com.drexask.reduplicate.scanMethodSelector.presentation.SettingsBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DuplicateFinderFragment : Fragment() {

    private val viewModel: DuplicateFinderFragmentViewModel by viewModels()

    private var _binding: FragmentDuplicateFinderBinding? = null
    private val binding get() = _binding!!

    private var progressStateFlowJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicateFinderBinding.inflate(layoutInflater)

        setBundleDataToViewModel()
        setupFolderFileDoc()
        setupListeners()
        setupObservers()

        return binding.root
    }

    private fun setBundleDataToViewModel() {
        val bundle = arguments
        val treeUri = when {
            SDK_INT > 33 -> bundle?.getParcelable(TREE_URI, Uri::class.java)
            else -> @Suppress("DEPRECATION") bundle?.getParcelable(TREE_URI)
        }
        viewModel.treeUriLD.value = treeUri
    }

    private fun setupFolderFileDoc() {
        val context = requireContext()
        viewModel.folderFileDoc =
            viewModel.treeUriLD.value!!.let { treeUri ->
                DocumentFile.fromTreeUri(context, treeUri)
                    ?: throw Error("Something is wrong with the Uri")
            }
    }

    private fun setupObservers() {
        viewModel.treeUriLD.observe(viewLifecycleOwner, treeUriObserver)
        viewModel.numberOfProcessedFilesLD.observe(viewLifecycleOwner, scanProgressObserver)
        viewModel.finderState.observe(viewLifecycleOwner, finderStateObserver)
    }

    private fun showSettingsDialog() {
        val dialog = SettingsBottomSheetDialogFragment(R.layout.bottom_sheet_dialog_settings)
        dialog.setStyle(
            BottomSheetDialogFragment.STYLE_NORMAL,
            R.style.CustomBottomSheetDialogTheme
        )
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

            if (viewModel.mainActivitySharedData.useFileNamesLD.value == false
                && viewModel.mainActivitySharedData.useFileWeightsLD.value == false
            ) {
                showSettingsDialog()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {

                viewModel.startDuplicatesFindProcess()

                withContext(Dispatchers.Main) {
                    if (viewModel.mainActivitySharedData.foundDuplicatesList!!.isEmpty()) { //TODO("Change that behavior")
                        val snackBar = Snackbar.make(
                            binding.constraintLayout,
                            R.string.no_duplicates_found,
                            Snackbar.LENGTH_LONG
                        )
                        snackBar.setAction(R.string.snack_bar_action_button_show_settings) {
                            showSettingsDialog()
                        }
                        snackBar.show()
                    } else {
                        findNavController().navigate(R.id.action_duplicateFinderFragment_to_duplicatePrioritySelectorFragment)
                    }
                }
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

    private val scanProgressObserver = Observer<Int> {
        binding.progressCircular.progress = it
        binding.tvCurrentProgress.text = getString(R.string.current_progress, it, viewModel.itemsQuantityInSelectedFolder)
    }

    private val finderStateObserver = Observer<CurrentFinderState> {
        when(it) {
            CurrentFinderState.IDLE -> {
                binding.progressCircular.isIndeterminate = false
                binding.progressCircular.visibility = View.INVISIBLE
            }
            CurrentFinderState.SCAN_FOR_ITEM_COUNT -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.progressCircular.isIndeterminate = true
            }
            CurrentFinderState.SCAN_FOR_DUPLICATES -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.progressCircular.isIndeterminate = false
                binding.progressCircular.max = viewModel.itemsQuantityInSelectedFolder ?: throw Exception()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        progressStateFlowJob?.cancel()
    }
}
