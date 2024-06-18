package com.drexask.reduplicate.duplicatePrioritySelector.domain.usecase

import android.net.Uri
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.core.utils.removeFileFromUri
import javax.inject.Inject

class SetDuplicatesHighlightedLinesByPriorityListUseCase @Inject constructor() {
    fun execute(duplicatesList: List<DuplicateWithHighlightedLine>,
                priorityList: List<Uri>) {
        duplicatesList.map { duplicate ->
            var highestPriority = Int.MAX_VALUE
            var indexOfHighestPriorityUriIndexFound = 0

            duplicate.duplicateFilesInnerList.mapIndexed { index, storageFile ->
                val currentPriority =
                    priorityList.indexOf(storageFile.file.uri.removeFileFromUri())
                if (currentPriority < highestPriority) {
                    highestPriority = currentPriority
                    indexOfHighestPriorityUriIndexFound = index
                }
            }
            duplicate.highlightedLineIndex = indexOfHighestPriorityUriIndexFound
        }
    }
}