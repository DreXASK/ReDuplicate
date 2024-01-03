package com.drexask.reduplicate.presentation.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.DuplicateFinderFragmentViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.TAB
import com.drexask.reduplicate.TREE_URI
import com.drexask.reduplicate.databinding.FragmentDuplicateFinderBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DuplicateFinderFragment : Fragment() {

    private val viewModel: DuplicateFinderFragmentViewModel by viewModels()

    private var _binding: FragmentDuplicateFinderBinding? = null
    private val binding get() = _binding!!

    var progressStateFlowJob: Job? = null

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
        val treeUri = bundle?.getParcelable<Uri>(TREE_URI)
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

        progressStateFlowJob = CoroutineScope(Dispatchers.Main).launch {
            viewModel.getDuplicatesListUseCase.stateFlow.collect {
                binding.progressCircular.progress = it
                binding.tvCurrentProgress.text = getString(R.string.current_progress, it, viewModel.itemsQuantityInSelectedFolder)
            }
        }

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

            binding.progressCircular.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                binding.progressCircular.isIndeterminate = true
                viewModel.scanFolder()
                binding.progressCircular.max =
                    viewModel.scanForItemsQuantityInSelectedFolderAndCacheIt()
                binding.progressCircular.isIndeterminate = false

                viewModel.numberOfProcessedFilesLD.postValue(0)
                viewModel.collectFindingProgressFlow()
                viewModel.getDuplicates()
                viewModel.getURIsPrioritySet()

                println(viewModel.mainActivitySharedData.foundDuplicatesList!!.joinToString(TAB))
                println(viewModel.mainActivitySharedData.foundDuplicatesList!!.size)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        progressStateFlowJob?.cancel()
    }
}
