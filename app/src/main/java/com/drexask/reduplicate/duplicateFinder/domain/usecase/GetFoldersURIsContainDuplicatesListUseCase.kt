package com.drexask.reduplicate.duplicateFinder.domain.usecase

import android.net.Uri
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.core.utils.removeFileFromUri
import javax.inject.Inject

class GetFoldersURIsContainDuplicatesListUseCase @Inject constructor() {

    fun execute(duplicatesList: List<DuplicateWithHighlightedLine>) : MutableList<Uri> {
        val mFoldersWithPrioritySet = mutableSetOf<Uri>()

        duplicatesList.map { duplicate ->
            duplicate.duplicateFilesInnerList.map {
                val folderUri = it.file.uri.removeFileFromUri()
                mFoldersWithPrioritySet.add(folderUri)
            }
        }

        return mFoldersWithPrioritySet.toMutableList()
    }
}
