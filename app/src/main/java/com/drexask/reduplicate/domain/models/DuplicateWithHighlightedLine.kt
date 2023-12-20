package com.drexask.reduplicate.domain.models

import com.drexask.reduplicate.storagetools.StorageFile

class DuplicateWithHighlightedLine(
    duplicatesSharedParameters: String,
    duplicateFilesInnerList: MutableList<StorageFile>,
    override var highlightedLineIndex: Int,
    override var collapsed: Boolean
): Duplicate(duplicatesSharedParameters, duplicateFilesInnerList), Collapsable, Highlightable
