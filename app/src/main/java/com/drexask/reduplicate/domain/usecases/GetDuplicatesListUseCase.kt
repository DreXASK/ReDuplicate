package com.drexask.reduplicate.domain.usecases

import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.storagetools.StorageFile
import com.drexask.reduplicate.storagetools.StorageFolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.StringBuilder
import javax.inject.Inject

class GetDuplicatesListUseCase @Inject constructor() {

    private val duplicatesMap = emptyMap<String, MutableList<StorageFile>>().toMutableMap()

    private lateinit var settings: DuplicatesFindSettings

    private var numberOfProcessedFiles = 0


    fun execute(settings: DuplicatesFindSettings, scannedFolder: StorageFolder) : List<DuplicateWithHighlightedLine> {
        this.settings = settings
        fillDuplicatesMap(scannedFolder)

        val duplicatesList = emptyList<DuplicateWithHighlightedLine>().toMutableList()

        duplicatesMap.map {
            if (it.value.size > 1)
                duplicatesList.add(DuplicateWithHighlightedLine(it.key, it.value, 0, false))
        }

        return duplicatesList
    }

    suspend fun getFindingProgressFlow(): Flow<Int> {
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