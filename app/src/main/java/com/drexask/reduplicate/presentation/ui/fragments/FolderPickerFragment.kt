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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderPickerFragment : Fragment() {

    private val viewModel: MainNavGraphViewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)

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

        return binding.root
    }

    private fun setupListeners() {
        clickTakePermission()
    }

    private fun clickTakePermission() {
        binding.btnTakePermission.setOnClickListener {
            getFolderPermission()
        }
    }

    private fun setupResultLaunchers() {

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

    private fun getFolderPermission() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            .putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory())
        activityFolderPickerResultLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}