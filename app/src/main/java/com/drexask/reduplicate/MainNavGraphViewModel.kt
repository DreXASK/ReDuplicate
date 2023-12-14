package com.drexask.reduplicate

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drexask.reduplicate.storagetools.StorageFile
import com.drexask.reduplicate.storagetools.StorageFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class MainNavGraphViewModel @Inject constructor() : ViewModel() {

    val treeUri = MutableLiveData<Uri>()
    val folderFileDoc = MutableLiveData<DocumentFile>()

    val useFileNames = MutableLiveData<Boolean>().also { it.value = true }
    val useFileHashes = MutableLiveData<Boolean>().also { it.value = false }
    val useFileWeights = MutableLiveData<Boolean>().also { it.value = false }

    private val _duplicatesMap =
        MutableLiveData<MutableMap<String, MutableList<StorageFile>>>().also {
            it.value = emptyMap<String, MutableList<StorageFile>>().toMutableMap()
        }  //TODO: Check if it'll be properly observed
    val duplicatesMap
        get() = _duplicatesMap as LiveData<MutableMap<String, MutableList<StorageFile>>>


    private var scannedFolder: StorageFolder? = null
    private var itemsQuantityInSelectedFolder: Int? = null
    val numberOfProcessedFiles = MutableLiveData<Int>()


    fun scanFolder() {
        if (folderFileDoc.value == null)
            throw Exception("folderFileDoc.value cannot be null here")
        scannedFolder = StorageFolder(folderFileDoc.value!!).also { it.scanFolderForStoredItems() }
    }

    fun getItemsQuantityInSelectedFolderAndRememberIt(): Int {
        if (scannedFolder == null)
            throw Exception("scannedFolder cannot be null here")

        return itemsQuantityInSelectedFolder ?: scannedFolder!!.getStoredFilesQuantity().also {
            itemsQuantityInSelectedFolder = it
        }
    }

    fun clearDuplicatesMap() {
        _duplicatesMap.value?.clear()
    }

    fun fillDuplicatesMap(folder: StorageFolder? = scannedFolder) {
        if (folder == null)
            throw Exception("Scanned folder cannot be null")

        folder.storedItems.map {
            when (it) {
                is StorageFolder -> fillDuplicatesMap(it)
                is StorageFile -> {
                    addFileToDuplicatesMapUsingSettings(it)
                    numberOfProcessedFiles.value = numberOfProcessedFiles.value?.plus(1)
                }
            }

        }
    }

    private fun addFileToDuplicatesMapUsingSettings(file: StorageFile) {
        val duplicatesMapKey = StringBuilder()
        if (useFileNames.value == true)
            duplicatesMapKey.append(file.file.name)
        if (useFileHashes.value == true)
            duplicatesMapKey.append(file.file.lastModified()).also { TODO("Need to delete this") }
        if (useFileWeights.value == true)
            duplicatesMapKey.append(file.file.length())

        addOrUpdateValueToDuplicatesMap(duplicatesMapKey.toString(), file)
    }

    private fun addOrUpdateValueToDuplicatesMap(duplicatesMapKey: String, file: StorageFile) {
        if (_duplicatesMap.value == null)
            throw Exception("DuplicatesMap value cannot be null at this function")

        if (_duplicatesMap.value!!.containsKey(duplicatesMapKey))
            _duplicatesMap.value!![duplicatesMapKey]!!.add(file)
        else
            _duplicatesMap.value!![duplicatesMapKey] = mutableListOf(file)
    }
}