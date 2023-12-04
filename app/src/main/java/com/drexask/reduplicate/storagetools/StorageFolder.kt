package com.drexask.reduplicate.storagetools

import androidx.documentfile.provider.DocumentFile
import com.drexask.reduplicate.TAB

data class StorageFolder(
    override var file: DocumentFile,
    val storedItems: MutableList<Storable>
): Storable {
    fun printTree(tabCount: Int = 0) {
        storedItems.map {
            repeat(tabCount) {
                print(TAB)
            }
            when (it) {
                is StorageFile ->  println(it.file.name)
                is StorageFolder -> {
                    println(it.file.name)
                    it.printTree(tabCount + 1)
                }
            }
        }
    }

    fun getStoredFilesQuantity(): Int {
        var returnValueInt = 0
        storedItems.map {
            returnValueInt += when(it) {
                is StorageFile -> 1
                is StorageFolder -> it.getStoredFilesQuantity()
                else -> throw Error("Unexpected type")
            }
        }
        return returnValueInt
    }
}