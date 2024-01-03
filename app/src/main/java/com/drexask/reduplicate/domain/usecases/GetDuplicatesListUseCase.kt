package com.drexask.reduplicate.domain.usecases

import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.storagetools.StorageFile
import com.drexask.reduplicate.storagetools.StorageFolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.StringBuilder
import javax.inject.Inject

class GetDuplicatesListUseCase @Inject constructor() {

    private lateinit var settings: DuplicatesFindSettings

    private var _stateFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    val stateFlow: StateFlow<Int>
        get() = _stateFlow

    fun execute(
        settings: DuplicatesFindSettings,
        scannedFolder: StorageFolder
    ): List<DuplicateWithHighlightedLine> {

        _stateFlow.value = 0

        this.settings = settings

        val duplicatesMap = emptyMap<String, MutableList<StorageFile>>().toMutableMap()
        val duplicatesList = emptyList<DuplicateWithHighlightedLine>().toMutableList()

        fillDuplicatesMap(duplicatesMap, scannedFolder)
        duplicatesMap.map {
            if (it.value.size > 1)
                duplicatesList.add(DuplicateWithHighlightedLine(it.key, it.value, 0, false))
        }

        return duplicatesList
    }

    private fun fillDuplicatesMap(
        duplicatesMap: MutableMap<String, MutableList<StorageFile>>,
        folder: StorageFolder
    ) {
        folder.storedItems.map {
            when (it) {
                is StorageFolder -> fillDuplicatesMap(duplicatesMap, it)
                is StorageFile -> {
                    addFileToDuplicatesMapUsingSettings(duplicatesMap, it)
                    _stateFlow.value += 1
                }
            }
        }
    }

    private fun addFileToDuplicatesMapUsingSettings(
        duplicateMap: MutableMap<String, MutableList<StorageFile>>,
        file: StorageFile
    ) {
        val duplicatesMapKey = StringBuilder()
        if (settings.useFileNames)
            duplicatesMapKey.append(file.file.name)
        if (settings.useFileWeights)
            duplicatesMapKey.append(file.file.length())

        addOrUpdateValueToDuplicatesMap(duplicateMap, duplicatesMapKey.toString(), file)
    }

    private fun addOrUpdateValueToDuplicatesMap(
        duplicatesMap: MutableMap<String, MutableList<StorageFile>>,
        key: String,
        file: StorageFile
    ) {
        if (duplicatesMap.containsKey(key))
            duplicatesMap[key]!!.add(file)
        else
            duplicatesMap[key] = mutableListOf(file)
    }

}