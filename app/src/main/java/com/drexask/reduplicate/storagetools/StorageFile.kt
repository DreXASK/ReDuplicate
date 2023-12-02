package com.drexask.reduplicate.storagetools

import androidx.documentfile.provider.DocumentFile

data class StorageFile(
    override var file: DocumentFile
) : Storable