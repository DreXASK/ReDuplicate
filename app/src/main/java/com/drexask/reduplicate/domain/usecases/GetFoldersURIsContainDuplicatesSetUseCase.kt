package com.drexask.reduplicate.domain.usecases

import android.net.Uri
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import javax.inject.Inject

class GetFoldersURIsContainDuplicatesSetUseCase @Inject constructor() {

    fun execute(duplicatesList: List<DuplicateWithHighlightedLine>) : MutableSet<Uri> {
        val mutableFoldersWithPrioritySet = mutableSetOf<Uri>()

        duplicatesList.map { duplicate ->
            duplicate.duplicateFilesInnerList.map {
                val folderUri = removeFileFromUri(it.file.uri)
                mutableFoldersWithPrioritySet.add(folderUri)
            }
        }

        return mutableFoldersWithPrioritySet
    }

    private fun removeFileFromUri(uri: Uri): Uri {

        val uriStringSegments = uri.toString().split("%2F")

        val uriStringSegmentsWithoutFile =
            uriStringSegments.subList(0, uriStringSegments.size - 1) // exclusive end value

        val uriStringWithoutFile = uriStringSegmentsWithoutFile.joinToString("%2F")

        return Uri.parse(uriStringWithoutFile)
    }
}
