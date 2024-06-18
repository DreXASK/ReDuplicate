package com.drexask.reduplicate.duplicateFinder.domain.models

import androidx.documentfile.provider.DocumentFile

interface Storable {
    var file: DocumentFile
}