package com.drexask.reduplicate.domain.usecases

import com.drexask.reduplicate.domain.models.Duplicates
import com.drexask.reduplicate.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.storagetools.StorageFile
import com.drexask.reduplicate.storagetools.StorageFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.StringBuilder
import javax.inject.Inject

class GetDuplicatesUseCase @Inject constructor() {

    private val duplicatesMap = emptyMap<String, MutableList<StorageFile>>().toMutableMap()

    private lateinit var settings: DuplicatesFindSettings

    private var numberOfProcessedFiles = 0


    fun execute(settings: DuplicatesFindSettings, scannedFolder: StorageFolder) : Duplicates {
        this.settings = settings

        fillDuplicatesMap(scannedFolder)

        println("Говорит execute: " + Thread.currentThread())

        return Duplicates(duplicatesMap)
    }

    suspend fun getProgressFlow(): Flow<Int> {
        var pastValue = 0
        return flow {
            while(true) {
                if (numberOfProcessedFiles > pastValue) {
                    pastValue = numberOfProcessedFiles
                    emit(numberOfProcessedFiles)
                }
                delay(10L)
            }
        }
    }

    private fun fillDuplicatesMap(folder: StorageFolder) {
        folder.storedItems.map {
            when (it) {
                is StorageFolder -> fillDuplicatesMap(it)
                is StorageFile -> {
                    addFileToDuplicatesMapUsingSettings(it)
                    numberOfProcessedFiles += 1
                }
            }
        }
        println("Говорит fillDuplicatesmap: " + Thread.currentThread())
    }

    private fun addFileToDuplicatesMapUsingSettings(file: StorageFile) {
        val duplicatesMapKey = StringBuilder()
        if (settings.useFileNames)
            duplicatesMapKey.append(file.file.name)
        if (settings.useFileWeights)
            duplicatesMapKey.append(file.file.length())

        addOrUpdateValueToDuplicatesMap(duplicatesMapKey.toString(), file)
    }

    private fun addOrUpdateValueToDuplicatesMap(duplicatesMapKey: String, file: StorageFile) {
        if (duplicatesMap.containsKey(duplicatesMapKey))
            duplicatesMap[duplicatesMapKey]!!.add(file)
        else
            duplicatesMap[duplicatesMapKey] = mutableListOf(file)
    }

}