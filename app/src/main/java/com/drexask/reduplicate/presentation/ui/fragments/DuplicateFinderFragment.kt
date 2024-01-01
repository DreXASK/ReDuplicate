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
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.DuplicateFinderFragmentViewModel
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.TREE_URI
import com.drexask.reduplicate.databinding.FragmentDuplicateFinderBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DuplicateFinderFragment : Fragment() {

    private val mainViewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)

    private val viewModel: DuplicateFinderFragmentViewModel by activityViewModels()

    private var _binding: FragmentDuplicateFinderBinding? = null
    private val binding get() = _binding!!

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
        viewModel.numberOfProcessedFilesLD.observe(
            viewLifecycleOwner,
            numberOfProcessedFilesObserver
        )
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

            if (viewModel.useFileNamesLD.value == false
                && viewModel.useFileWeightsLD.value == false
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


                viewModel.collectFindingProgressFlow()
                mainViewModel.foundDuplicatesList = viewModel.getDuplicates().toMutableList()
                //viewModel.getURIsPrioritySet()

                if (mainViewModel.foundDuplicatesList.isEmpty())
                    withContext(Dispatchers.Main) { //TODO("Change that behavior")
                        Toast.makeText(
                            context,
                            getString(R.string.no_duplicates_found), Toast.LENGTH_LONG
                        ).show()
                } else {
                    withContext(Dispatchers.Main) {
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