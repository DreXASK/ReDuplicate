package com.drexask.reduplicate

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.domain.usecases.GetFoldersURIsContainDuplicatesListUseCase
import com.drexask.reduplicate.domain.usecases.SetDuplicatesHighlightedLinesByPriorityListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class DuplicatePrioritySelectorViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var mainActivitySharedData: MainActivitySharedData

    @Inject
    lateinit var setDuplicatesHighlightedLinesByPriorityListUseCase: SetDuplicatesHighlightedLinesByPriorityListUseCase

    fun swapUriPriorities(fromPosition: Int, toPosition: Int) {
        Collections.swap(
            mainActivitySharedData.uRIsContainDuplicatesPriorityList!!,
            fromPosition,
            toPosition
        )
    }

    fun setDuplicatesHighlightedLinesByPriorityList() {
        setDuplicatesHighlightedLinesByPriorityListUseCase.execute(
            mainActivitySharedData.foundDuplicatesList!!,
            mainActivitySharedData.uRIsContainDuplicatesPriorityList!!
        )
    }
}