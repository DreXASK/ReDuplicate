package com.drexask.reduplicate.duplicateFinder.presentation

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.MainActivitySharedData
import com.drexask.reduplicate.duplicateFinder.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.duplicateFinder.domain.usecase.GetDuplicatesListUseCase
import com.drexask.reduplicate.duplicateFinder.domain.usecase.GetFoldersURIsContainDuplicatesListUseCase
import com.drexask.reduplicate.duplicateFinder.domain.models.StorageFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuplicateFinderFragmentViewModel @Inject constructor() : ViewModel() {

    @Inject lateinit var getFoldersURIsContainDuplicatesListUseCase: GetFoldersURIsContainDuplicatesListUseCase
    @Inject lateinit var mainActivitySharedData: MainActivitySharedData
    @Inject lateinit var getDuplicatesListUseCase: GetDuplicatesListUseCase

    val treeUriLD = MutableLiveData<Uri>()
    lateinit var folderFileDoc: DocumentFile

    val numberOfProcessedFilesLD = MutableLiveData<Int>()

    private var scannedFolder: StorageFolder? = null
    var itemsQuantityInSelectedFolder: Int? = null

    fun getURIsPrioritySet() {
        mainActivitySharedData.uRIsContainDuplicatesPriorityList =
            getFoldersURIsContainDuplicatesListUseCase.execute(mainActivitySharedData.foundDuplicatesList!!)
    }

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

    suspend fun getDuplicates() {
        if (scannedFolder == null)
            throw Exception("scannedFolder cannot be null here")

        val settings = DuplicatesFindSettings(
             mainActivitySharedData.useFileNamesLD.value ?: throw Exception("useFileNames.value cannot be null here"),
            mainActivitySharedData.useFileWeightsLD.value ?: throw Exception("useFileWeights.value cannot be null here")
        )

        mainActivitySharedData.foundDuplicatesList = viewModelScope.async(Dispatchers.Default) {
            getDuplicatesListUseCase.execute(settings, scannedFolder!!)
        }.await()
    }

    fun collectFindingProgressFlow() {
        viewModelScope.launch(Dispatchers.Default + SupervisorJob()) {
            getDuplicatesListUseCase.stateFlow.collect {
                numberOfProcessedFilesLD.postValue(it)
            }
        }
    }

}