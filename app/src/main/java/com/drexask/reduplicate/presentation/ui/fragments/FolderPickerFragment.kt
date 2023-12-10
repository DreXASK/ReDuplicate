package com.drexask.reduplicate.presentation.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.TREE_URI
import com.drexask.reduplicate.databinding.FragmentFolderPickerBinding
import com.drexask.reduplicate.domain.usecases.ChooseFolderUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FolderPickerFragment : Fragment() {

    private val viewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)

    private lateinit var activityFolderPickerResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var chooseFolderUseCase: ChooseFolderUseCase

    private var _binding: FragmentFolderPickerBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFolderPickerBinding.inflate(layoutInflater)

        setupListeners()
        setupResultLaunchers()

        chooseFolderUseCase = ChooseFolderUseCase(activityFolderPickerResultLauncher)

        return binding.root
    }

    private fun setupListeners() {
        clickTakePermission()
    }


    private fun clickTakePermission() {
        binding.btnTakePermission.setOnClickListener {
            chooseFolderUseCase.execute()
        }
    }

    private fun setupResultLaunchers() {
        setupFolderPickerResultLauncher()
    }

    private fun setupFolderPickerResultLauncher() {

        activityFolderPickerResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                val treeUri = result.data?.data
                treeUri?.let { viewModel.treeUri.value = it }

                findNavController().navigate(R.id.action_folderPickerFragment_to_duplicateFinderFragment)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}