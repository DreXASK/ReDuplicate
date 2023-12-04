package com.drexask.reduplicate

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drexask.reduplicate.storagetools.StorageFolder
import com.drexask.reduplicate.storagetools.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuplicateFinderFragmentViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {
    val treeUri = MutableLiveData<Uri>()
    val folderFileDoc = MutableLiveData<DocumentFile>()
    val storageFolder: StorageFolder?
        get() {
            return folderFileDoc.value?.let { file ->
                storageManager.scanAndReturnFolder(file)
            }
        }
    val itemsQuantityInSelectedFolder: Int?
        get() {
            return treeUri.value?.let {
                folderFileDoc.value?.let {
                    storageFolder?.getStoredFilesQuantity()
                }
            }
        }
}