package com.drexask.reduplicate.duplicateFinder.domain.models

import androidx.documentfile.provider.DocumentFile

data class StorageFile(
    override var file: DocumentFile
) : Storable