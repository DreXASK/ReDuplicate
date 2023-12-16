package com.drexask.reduplicate.domain.models

import com.drexask.reduplicate.storagetools.StorageFile

class Duplicate(
    val duplicatesSharedParameters: String,
    val duplicateFilesInnerList: MutableList<StorageFile>
)