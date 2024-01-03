package com.drexask.reduplicate.domain.models

import com.drexask.reduplicate.storagetools.StorageFile

class DuplicateWithHighlightedLine(
    duplicatesSharedParameters: String, // Common name or weight of duplicates in inner list
    duplicateFilesInnerList: MutableList<StorageFile>,
    override var highlightedLineIndex: Int, // Highlighted line of duplicate in recyclerView that will be saved in the future
    override var collapsed: Boolean // Denoting is it collapsed in recyclerView
) : Duplicate(duplicatesSharedParameters, duplicateFilesInnerList), Collapsable, Highlightable