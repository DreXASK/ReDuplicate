package com.drexask.reduplicate.storagetools

import androidx.documentfile.provider.DocumentFile
import com.drexask.reduplicate.TAB

data class StorageFolder(
    override var file: DocumentFile,
    private val _storedItems: MutableList<Storable> = emptyList<Storable>().toMutableList()
) : Storable {

    val storedItems: List<Storable>
        get() = _storedItems

    fun printTree(tabCount: Int = 0) {
        _storedItems.map {
            repeat(tabCount) {
                print(TAB)
            }
            when (it) {
                is StorageFile -> println(it.file.name)
                is StorageFolder -> {
                    println(it.file.name)
                    it.printTree(tabCount + 1)
                }
            }
        }
    }

    fun clearStoredItems() {
        _storedItems.clear()
    }

    fun scanFolderForStoredItems() {
        this.file.listFiles().map {
            if (it.isDirectory) _storedItems.add(
                StorageFolder(it).also { folder ->
                    folder.scanFolderForStoredItems()
                }
            )
            else _storedItems.add(StorageFile(it))
        }
    }

    fun getStoredFilesQuantity(): Int {
        var returnValueInt = 0
        _storedItems.map {
            returnValueInt += when (it) {
                is StorageFile -> 1
                is StorageFolder -> it.getStoredFilesQuantity()
                else -> throw Error("Unexpected type")
            }
        }
        return returnValueInt
    }
}