package com.drexask.reduplicate.duplicateRemover.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.MainActivitySharedData
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.duplicateRemover.domain.usecase.RemoveDuplicatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuplicateRemoverFragmentViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var mainActivitySharedData: MainActivitySharedData

    @Inject
    lateinit var removeDuplicatesUseCase: RemoveDuplicatesUseCase

    var areDuplicatesRemoved = false
    val numberOfRemovedFilesLD = MutableLiveData<Int>()
    val numberOfRemovedBytesLD = MutableLiveData<Long>()

    fun removeDuplicates(foundDuplicatesList: List<DuplicateWithHighlightedLine>) {
        areDuplicatesRemoved = true
        removeDuplicatesUseCase.execute(foundDuplicatesList)
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

}