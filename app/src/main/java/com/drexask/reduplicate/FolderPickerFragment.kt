package com.drexask.reduplicate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.drexask.reduplicate.databinding.FragmentFolderPickerBinding
import com.drexask.reduplicate.storagetools.StorageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FolderPickerFragment : Fragment() {

    @Inject
    lateinit var storageManager: StorageManager

    private lateinit var activityFolderPickerResultLauncher: ActivityResultLauncher<Intent>

    private var vb: FragmentFolderPickerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vb = FragmentFolderPickerBinding.inflate(layoutInflater)

        activityFolderPickerResultLauncher = setUpResultLauncher()

        vb?.btnTakePermission?.setOnClickListener {
            storageManager.getFolderPermission(activityFolderPickerResultLauncher)
        }

        return vb?.root
    }

    private fun setUpResultLauncher() : ActivityResultLauncher<Intent> {
        return registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                val treeUri = result.data?.data

                activity?.let { activity ->
                    treeUri?.let {treeUri ->
                        activity.contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        val folderFileDoc = DocumentFile.fromTreeUri(activity, treeUri)
                        val storageFolder = folderFileDoc?.let { file -> storageManager.scanAndReturnFolder(file) }

                        storageFolder?.printTree()
                    }
                }
            }
        }
    }
}