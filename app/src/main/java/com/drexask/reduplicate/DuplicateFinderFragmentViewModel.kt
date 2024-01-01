package com.drexask.reduplicate

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.domain.usecases.GetDuplicatesListUseCase
import com.drexask.reduplicate.storagetools.StorageFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuplicateFinderFragmentViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var getDuplicatesListUseCase: GetDuplicatesListUseCase

    val treeUriLD = MutableLiveData<Uri>()
    lateinit var folderFileDoc: DocumentFile

    val numberOfProcessedFilesLD = MutableLiveData<Int>()

    // For settings
    val useFileNamesLD = MutableLiveData<Boolean>().apply { value = true }
    val useFileWeightsLD = MutableLiveData<Boolean>().apply { value = false }

    private var scannedFolder: StorageFolder? = null
    private var itemsQuantityInSelectedFolder: Int? = null


    fun scanFolder() {
        scannedFolder =
            StorageFolder(folderFileDoc).also { it.scanFolderForStoredItems() }
    }

    fun scanForItemsQuantityInSelectedFolderAndCacheIt(): Int {
        if (scannedFolder == null)
            throw Exception("scannedFolder cannot be null here")

        return scannedFolder!!.getStoredFilesQuantity().also {
            itemsQuantityInSelectedFolder = it
        }
    }

    suspend fun getDuplicates() : List<DuplicateWithHighlightedLine> {
        if (scannedFolder == null)
            throw Exception("scannedFolder cannot be null here")

        val settings = DuplicatesFindSettings(
            useFileNamesLD.value ?: throw Exception("useFileNames.value cannot be null here"),
            useFileWeightsLD.value ?: throw Exception("useFileWeights.value cannot be null here")
        )

        val result = viewModelScope.async(Dispatchers.Default) {
            getDuplicatesListUseCase.execute(settings, scannedFolder!!)
        }
        return result.await()
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