package com.drexask.reduplicate.folderPrioritySettings.presentation

import androidx.lifecycle.ViewModel
import com.drexask.reduplicate.MainActivitySharedData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class FoldersPrioritySettingsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var mainActivitySharedData: MainActivitySharedData

    fun swapUriPriorities(fromPosition: Int, toPosition: Int) {
        Collections.swap(
            mainActivitySharedData.uRIsContainDuplicatesPriorityList!!,
            fromPosition,
            toPosition
        )
    }
}