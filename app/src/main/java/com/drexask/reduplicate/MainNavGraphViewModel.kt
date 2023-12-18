package com.drexask.reduplicate

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.domain.usecases.GetDuplicatesListUseCase
import com.drexask.reduplicate.domain.usecases.GetFoldersURIsContainDuplicatesSetUseCase
import com.drexask.reduplicate.storagetools.StorageFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavGraphViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var getFoldersURIsContainDuplicatesSetUseCase: GetFoldersURIsContainDuplicatesSetUseCase

    @Inject
    lateinit var getDuplicatesListUseCase: GetDuplicatesListUseCase

    val treeUri = MutableLiveData<Uri>()
    val folderFileDoc = MutableLiveData<DocumentFile>()

    val useFileNames = MutableLiveData<Boolean>().also { it.value = true }
    val useFileHashes = MutableLiveData<Boolean>().also { it.value = false }
    val useFileWeights = MutableLiveData<Boolean>().also { it.value = false }

    var foundDuplicatesList: List<DuplicateWithHighlightedLine>? = null
    var uRIsContainDuplicatesPrioritySet: MutableSet<Uri>? = null

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

    suspend fun getDuplicates() {
        if (scannedFolder == null)
            throw Exception("scannedFolder cannot be null here")

        val settings = DuplicatesFindSettings(
            useFileNames.value ?: throw Exception("useFileNames.value cannot be null here"),
            useFileWeights.value ?: throw Exception("useFileWeights.value cannot be null here")
        )

        viewModelScope.async(Dispatchers.Default) {
            foundDuplicatesList = getDuplicatesListUseCase.execute(settings, scannedFolder!!)
        }.await()
    }

    fun getURIsPrioritySet() {
        if (foundDuplicatesList == null)
            throw Exception("foundDuplicatesList cannot be null here")

        uRIsContainDuplicatesPrioritySet = getFoldersURIsContainDuplicatesSetUseCase.execute(foundDuplicatesList!!)
    }

    fun collectProgressFlow() {
        viewModelScope.launch(Dispatchers.Default + SupervisorJob()) {
            val progressFlow = getDuplicatesListUseCase.getProgressFlow()
            progressFlow.collect {
                numberOfProcessedFiles.postValue(it)
                if (it == itemsQuantityInSelectedFolder)
                    cancel()
            }
        }

    }
}