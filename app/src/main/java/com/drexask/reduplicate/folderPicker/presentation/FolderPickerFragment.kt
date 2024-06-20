package com.drexask.reduplicate.folderPicker.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.R
import com.drexask.reduplicate.duplicateFinder.utils.TREE_URI
import com.drexask.reduplicate.databinding.FragmentFolderPickerBinding
import com.drexask.reduplicate.folderPicker.domain.usecase.ChooseFolderUseCase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderPickerFragment : Fragment() {

    private lateinit var chooseFolderUseCase: ChooseFolderUseCase
    private lateinit var activityFolderPickerResultLauncher: ActivityResultLauncher<Intent>

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
                val bundle = Bundle().also { it.putParcelable(TREE_URI, treeUri) }
                findNavController().navigate(
                    R.id.action_folderPickerFragment_to_duplicateFinderFragment,
                    bundle
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}