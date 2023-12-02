package com.drexask.reduplicate

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.databinding.FragmentFolderPickerBinding
import com.drexask.reduplicate.storagetools.StorageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FolderPickerFragment : Fragment() {

    @Inject
    lateinit var storageManager: StorageManager

    private lateinit var activityFolderPickerResultLauncher: ActivityResultLauncher<Intent>

    private var _binding: FragmentFolderPickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFolderPickerBinding.inflate(layoutInflater)

        activityFolderPickerResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                val treeUri = result.data?.data


                activity?.let { activity ->
                    treeUri?.let { treeUri ->

                        val folderFileDoc = DocumentFile.fromTreeUri(activity, treeUri)
                        val storageFolder =
                            folderFileDoc?.let { file -> storageManager.scanAndReturnFolder(file) }

                        storageFolder?.printTree()
                    }
                }

                val bundle = Bundle().also { it.putParcelable(TREE_URI, treeUri) }
                findNavController().navigate(R.id.action_folderPickerFragment_to_duplicateFinderFragment, bundle)

            }
        }

        binding.btnTakePermission.setOnClickListener {
            getFolderPermission()
        }

        return binding.root
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