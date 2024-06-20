package com.drexask.reduplicate.duplicateRemover.domain.usecase

import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor() {

    private var numberOfRemovedFiles = 0
    private var numberOfRemovedBytes = 0L

    private val _stateFlow: MutableStateFlow<Pair<Int, Long>> = MutableStateFlow(Pair(0, 0L))
    internal val stateFlow: StateFlow<Pair<Int, Long>>
        get() = _stateFlow

    fun execute(duplicateList: List<DuplicateWithHighlightedLine>) {
        duplicateList.map { duplicate ->
            duplicate.duplicateFilesInnerList.mapIndexed { index, storageFile ->
                if (index != duplicate.highlightedLineIndex) {
                    numberOfRemovedFiles++
                    numberOfRemovedBytes += storageFile.file.length()
                    //storageFile.file.delete() TODO("Turned off to test the application!!!")
                    _stateFlow.value = Pair(numberOfRemovedFiles, numberOfRemovedBytes)
                }
            }
        }
    }

}