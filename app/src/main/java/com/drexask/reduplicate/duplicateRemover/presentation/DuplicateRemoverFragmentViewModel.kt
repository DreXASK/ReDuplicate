package com.drexask.reduplicate.duplicateRemover.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.MainActivitySharedData
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.duplicateRemover.domain.usecase.RemoveDuplicatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class DuplicateRemoverFragmentViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var mainActivitySharedData: MainActivitySharedData

    @Inject
    lateinit var removeDuplicatesUseCase: RemoveDuplicatesUseCase

    internal var areDuplicatesRemoved = false

    private var _areDuplicatesBeingRemovedLD: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    internal val areDuplicatesBeingRemovedLD: LiveData<Boolean> = _areDuplicatesBeingRemovedLD

    val numberOfRemovedFilesAndBytesLD = MutableLiveData<Pair<Int, Long>>()

    fun removeDuplicates(foundDuplicatesList: List<DuplicateWithHighlightedLine>) {
        viewModelScope.launch(Dispatchers.IO) {
            _areDuplicatesBeingRemovedLD.postValue(true)

            areDuplicatesRemoved = true
            removeDuplicatesUseCase.execute(foundDuplicatesList)

            _areDuplicatesBeingRemovedLD.postValue(false)
        }
    }

    fun collectRemovingProgressFlow() {
        viewModelScope.launch(Dispatchers.Default + SupervisorJob()) {
            val progressFlow = removeDuplicatesUseCase.stateFlow
            progressFlow.collect {
                numberOfRemovedFilesAndBytesLD.postValue(it)
            }
        }
    }

}