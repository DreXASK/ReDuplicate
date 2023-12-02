package com.drexask.reduplicate.storagetools

import android.content.Intent
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.documentfile.provider.DocumentFile
import javax.inject.Inject

class StorageManager @Inject constructor() {

    fun scanAndReturnFolder(folderFileDoc: DocumentFile): StorageFolder {
        val storageFolder = StorageFolder(folderFileDoc, emptyList<StorageFile>().toMutableList())

        storageFolder.file.listFiles().map {
            if (it.isDirectory) storageFolder.storedItems.add(scanAndReturnFolder(it))
            else storageFolder.storedItems.add(StorageFile(it))
        }

        return storageFolder
    }
}