package com.drexask.reduplicate.domain.models

import android.net.Uri
import com.drexask.reduplicate.storagetools.StorageFile

class DuplicateWithHighlightedLine(
    duplicatesSharedParameters: String,
    duplicateFilesInnerList: MutableList<StorageFile>,
    var highlightedLineIndex: Int
): Duplicate(duplicatesSharedParameters, duplicateFilesInnerList)
