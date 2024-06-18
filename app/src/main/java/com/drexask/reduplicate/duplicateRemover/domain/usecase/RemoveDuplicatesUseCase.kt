package com.drexask.reduplicate.duplicateRemover.domain.usecase

import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor() {

    private var numberOfRemovedFiles = 0
    private var numberOfRemovedBytes = 0L

    fun execute(duplicateList: List<DuplicateWithHighlightedLine>) {
        duplicateList.map { duplicate ->
            duplicate.duplicateFilesInnerList.mapIndexed { index, storageFile ->
                if (index != duplicate.highlightedLineIndex) {
                    numberOfRemovedFiles++
                    numberOfRemovedBytes += storageFile.file.length()
                    //storageFile.file.delete() TODO("ATTENTION HERE!!!!!!!")
                }
            }
        }
    }

    suspend fun getRemovingProgressFlow(): Flow<Pair<Int, Long>> {
        var pastValue = 0
        return flow {
            while(true) {
                if (numberOfRemovedFiles > pastValue) {
                    pastValue = numberOfRemovedFiles
                    emit(Pair(numberOfRemovedFiles, numberOfRemovedBytes))
                }
                delay(10L)
            }
        }
    }

}