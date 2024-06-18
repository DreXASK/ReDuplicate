package com.drexask.reduplicate.core.domain.models

import com.drexask.reduplicate.duplicateFinder.domain.models.StorageFile
open class Duplicate(
    val duplicatesSharedParameters: String,
    val duplicateFilesInnerList: MutableList<StorageFile>
)