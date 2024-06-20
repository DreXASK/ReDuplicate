package com.drexask.reduplicate.core.domain.models

import com.drexask.reduplicate.duplicateFinder.domain.models.StorageFile

class DuplicateWithHighlightedLine(
    duplicatesSharedParameters: String, // Common name or weight of duplicates in inner list
    duplicateFilesInnerList: List<StorageFile>, // List of files in storage that have common duplicatesSharedParameters
    override var highlightedLineIndex: Int, // Highlighted line of duplicate in recyclerView that will be saved in the future
    override var collapsed: Boolean // Denoting whether it collapsed in recyclerView
) : Duplicate(duplicatesSharedParameters, duplicateFilesInnerList), Collapsable, Highlightable