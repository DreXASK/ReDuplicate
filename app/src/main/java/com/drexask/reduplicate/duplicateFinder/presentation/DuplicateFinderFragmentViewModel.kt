package com.drexask.reduplicate.duplicateFinder.presentation

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.MainActivitySharedData
import com.drexask.reduplicate.duplicateFinder.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.duplicateFinder.domain.usecase.GetDuplicatesListUseCase
import com.drexask.reduplicate.duplicateFinder.domain.usecase.GetFoldersURIsContainDuplicatesListUseCase
import com.drexask.reduplicate.duplicateFinder.domain.models.StorageFolder
import com.drexask.reduplicate.duplicateFinder.utils.TAB
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuplicateFinderFragmentViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var getFoldersURIsContainDuplicatesListUseCase: GetFoldersURIsContainDuplicatesListUseCase
    @Inject
    lateinit var mainActivitySharedData: MainActivitySharedData
    @Inject
    lateinit var getDuplicatesListUseCase: GetDuplicatesListUseCase

    internal val finderState =
        MutableLiveData<FinderState>().apply { this.value = FinderState.IDLE }

    lateinit var folderFileDoc: DocumentFile
    internal val treeUriLD = MutableLiveData<Uri>()

    private val _numberOfProcessedFilesLD = MutableLiveData<Int>()
    internal val numberOfProcessedFilesLD: LiveData<Int> = _numberOfProcessedFilesLD

    private var scannedFolder: StorageFolder? = null
    internal var itemsQuantityInSelectedFolder: Int? = null

    suspend fun startDuplicatesFindProcess() {
        finderState.postValue(FinderState.SCAN_FOR_ITEM_COUNT)
        scanFolder()
        scanForItemsQuantityInSelectedFolderAndCacheIt()

        finderState.postValue(FinderState.SCAN_FOR_DUPLICATES)
        _numberOfProcessedFilesLD.postValue(0)
        collectFindingProgressFlow()
        getDuplicates()
        getURIsPrioritySet()

        finderState.postValue(FinderState.IDLE)
        println(mainActivitySharedData.foundDuplicatesList!!.joinToString(TAB))
        println(mainActivitySharedData.foundDuplicatesList!!.size)
    }

    private fun getURIsPrioritySet() {
        mainActivitySharedData.uRIsContainDuplicatesPriorityList =
            getFoldersURIsContainDuplicatesListUseCase.execute(mainActivitySharedData.foundDuplicatesList!!)
    }

    private fun scanFolder() {
        scannedFolder =
            StorageFolder(folderFileDoc).also { it.scanFolderForStoredItems() }
    }

    private fun scanForItemsQuantityInSelectedFolderAndCacheIt(): Int {
        if (scannedFolder == null)
            throw Exception("scannedFolder cannot be null here")

        return scannedFolder!!.getStoredFilesQuantity().also {
            itemsQuantityInSelectedFolder = it
        }
    }

    private suspend fun getDuplicates() {
        if (scannedFolder == null)
            throw Exception("scannedFolder cannot be null here")

        val settings = DuplicatesFindSettings(
            mainActivitySharedData.useFileNamesLD.value
                ?: throw Exception("useFileNames.value cannot be null here"),
            mainActivitySharedData.useFileWeightsLD.value
                ?: throw Exception("useFileWeights.value cannot be null here")
        )

        mainActivitySharedData.foundDuplicatesList = viewModelScope.async(Dispatchers.Default) {
            getDuplicatesListUseCase.execute(settings, scannedFolder!!)
        }.await()
    }

    private fun collectFindingProgressFlow() {
        viewModelScope.launch(Dispatchers.Default + SupervisorJob()) {
            getDuplicatesListUseCase.stateFlow.collect {
                _numberOfProcessedFilesLD.postValue(it)
            }
        }
    }
}

internal enum class FinderState {
    IDLE,
    SCAN_FOR_ITEM_COUNT,
    SCAN_FOR_DUPLICATES
}