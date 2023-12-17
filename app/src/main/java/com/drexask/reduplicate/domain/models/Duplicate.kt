package com.drexask.reduplicate.domain.models

import com.drexask.reduplicate.storagetools.StorageFile
open class Duplicate(
    val duplicatesSharedParameters: String,
    val duplicateFilesInnerList: MutableList<StorageFile>
)