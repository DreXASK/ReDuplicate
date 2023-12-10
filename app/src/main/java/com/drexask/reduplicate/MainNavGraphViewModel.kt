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
class MainNavGraphViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {

    val treeUri = MutableLiveData<Uri>()
    val folderFileDoc = MutableLiveData<DocumentFile>()

    val useFileNames = MutableLiveData<Boolean>().also { it.value = false }
    val useFileHashes = MutableLiveData<Boolean>().also { it.value = false }
    val useFileWeights = MutableLiveData<Boolean>().also { it.value = true }

    private var scannedFolder: StorageFolder? = null
    private var itemsQuantityInSelectedFolder: Int? = null

    fun getScannedFolderOrNull(): StorageFolder? {
        return when(scannedFolder) {
            is StorageFolder -> scannedFolder
            null -> folderFileDoc.value?.let { file ->
                scannedFolder = storageManager.scanAndReturnFolder(file)
                scannedFolder
            }
            else -> throw Exception("Unknown type")
        }
    }

    fun getItemsQuantityInSelectedFolderOrNull(): Int? {
        return when(itemsQuantityInSelectedFolder) {
            is Int -> itemsQuantityInSelectedFolder
            null -> treeUri.value?.let {
                folderFileDoc.value?.let {
                    itemsQuantityInSelectedFolder = getScannedFolderOrNull()?.getStoredFilesQuantity()
                    itemsQuantityInSelectedFolder
                }
            }
            else -> throw Exception("Unknown type")
        }
    }

}