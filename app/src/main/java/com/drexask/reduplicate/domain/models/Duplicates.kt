package com.drexask.reduplicate.domain.models

import com.drexask.reduplicate.storagetools.StorageFile

@JvmInline
value class Duplicates(val duplicatesMap: MutableMap<String, MutableList<StorageFile>>)