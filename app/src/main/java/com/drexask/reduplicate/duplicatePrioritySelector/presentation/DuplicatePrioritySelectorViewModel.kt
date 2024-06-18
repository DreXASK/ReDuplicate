package com.drexask.reduplicate.duplicatePrioritySelector.presentation

import androidx.lifecycle.ViewModel
import com.drexask.reduplicate.MainActivitySharedData
import com.drexask.reduplicate.duplicatePrioritySelector.domain.usecase.SetDuplicatesHighlightedLinesByPriorityListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Collections
import javax.inject.Inject

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