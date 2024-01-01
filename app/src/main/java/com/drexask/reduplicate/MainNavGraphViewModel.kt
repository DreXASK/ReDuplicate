package com.drexask.reduplicate

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.models.DuplicatesFindSettings
import com.drexask.reduplicate.domain.usecases.GetDuplicatesListUseCase
import com.drexask.reduplicate.domain.usecases.GetFoldersURIsContainDuplicatesListUseCase
import com.drexask.reduplicate.domain.usecases.RemoveDuplicatesUseCase
import com.drexask.reduplicate.domain.usecases.SetDuplicatesHighlightedLinesByPriorityListUseCase
import com.drexask.reduplicate.storagetools.StorageFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavGraphViewModel @Inject constructor() : ViewModel() {
    lateinit var foundDuplicatesList: MutableList<DuplicateWithHighlightedLine>
}
