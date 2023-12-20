package com.drexask.reduplicate

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.domain.usecases.GetDuplicatesListUseCase
import com.drexask.reduplicate.domain.usecases.GetFoldersURIsContainDuplicatesListUseCase
import com.drexask.reduplicate.domain.usecases.RemoveDuplicatesUseCase
import com.drexask.reduplicate.domain.usecases.SetDuplicatesHighlightedLinesByPriorityListUseCase
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
    lateinit var getFoldersURIsContainDuplicatesListUseCase: GetFoldersURIsContainDuplicatesListUseCase

    @Inject
    lateinit var getDuplicatesListUseCase: GetDuplicatesListUseCase

    @Inject
    lateinit var setDuplicatesHighlightedLinesByPriorityListUseCase: SetDuplicatesHighlightedLinesByPriorityListUseCase

    @Inject
    lateinit var removeDuplicatesUseCase: RemoveDuplicatesUseCase

    val treeUriLD = MutableLiveData<Uri>()
    val folderFileDocLD = MutableLiveData<DocumentFile>()

    // For settings
    val useFileNamesLD = MutableLiveData<Boolean>().apply { value = true }
    val useFileWeightsLD = MutableLiveData<Boolean>().apply { value = false }

    private var scannedFolder: StorageFolder? = null
    private var itemsQuantityInSelectedFolder: Int? = null

    var foundDuplicatesList: List<DuplicateWithHighlightedLine>? = null
    var mURIsContainDuplicatesPriorityList: MutableList<Uri>? = null

    val numberOfProcessedFilesLD = MutableLiveData<Int>()
    val numberOfRemovedFilesLD = MutableLiveData<Int>()
    val numberOfRemovedBytesLD = MutableLiveData<Long>()

    fun scanFolder() {
        if (folderFileDocLD.value == null)
            throw Exception("folderFileDoc.value cannot be null here")
        scannedFolder =
            StorageFolder(folderFileDocLD.value!!).also { it.scanFolderForStoredItems() }
    }

    fun getItemsQuantityInSelectedFolderAndCacheIt(): Int {
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
            useFileNamesLD.value ?: throw Exception("useFileNames.value cannot be null here"),
            useFileWeightsLD.value ?: throw Exception("useFileWeights.value cannot be null here")
        )

        viewModelScope.async(Dispatchers.Default) {
            foundDuplicatesList = getDuplicatesListUseCase.execute(settings, scannedFolder!!)
        }.await()
    }

    fun getURIsPrioritySet() {
        if (foundDuplicatesList == null)
            throw Exception("foundDuplicatesList cannot be null here")

        mURIsContainDuplicatesPriorityList =
            getFoldersURIsContainDuplicatesListUseCase.execute(foundDuplicatesList!!)
    }

    fun setDuplicatesHighlightedLinesByPriorityList() {
        if (foundDuplicatesList == null)
            throw Exception("foundDuplicatesList cannot be null here")
        if (mURIsContainDuplicatesPriorityList == null)
            throw Exception("mURIsContainDuplicatesPriorityList cannot be null here")

        setDuplicatesHighlightedLinesByPriorityListUseCase.execute(
            foundDuplicatesList!!,
            mURIsContainDuplicatesPriorityList!!
        )
    }

    fun removeDuplicates() {
        if (foundDuplicatesList == null)
            throw Exception("foundDuplicatesList cannot be null here")

        removeDuplicatesUseCase.execute(foundDuplicatesList!!)
    }

    fun collectRemovingProgressFlow() {
        viewModelScope.launch(Dispatchers.Default + SupervisorJob()) {
            val progressFlow = removeDuplicatesUseCase.getRemovingProgressFlow()
            progressFlow.collect {
                val numberOfRemovedFiles = it.first
                val numberOfRemovedBytes = it.second
                numberOfRemovedFilesLD.postValue(numberOfRemovedFiles)
                numberOfRemovedBytesLD.postValue(numberOfRemovedBytes)
            }
        }
    }

    fun collectFindingProgressFlow() {
        viewModelScope.launch(Dispatchers.Default + SupervisorJob()) {
            val progressFlow = getDuplicatesListUseCase.getFindingProgressFlow()
            progressFlow.collect {
                numberOfProcessedFilesLD.postValue(it)
                if (it == itemsQuantityInSelectedFolder)
                    cancel()
            }
        }

    }
}
