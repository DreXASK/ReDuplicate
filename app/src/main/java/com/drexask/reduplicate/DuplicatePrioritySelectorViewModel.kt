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
    lateinit var setDuplicatesHighlightedLinesByPriorityListUseCase: SetDuplicatesHighlightedLinesByPriorityListUseCase

    @Inject
    lateinit var getFoldersURIsContainDuplicatesListUseCase: GetFoldersURIsContainDuplicatesListUseCase

    lateinit var uRIsContainDuplicatesPriorityList: MutableList<Uri>

    fun swapUriPriorities(fromPosition: Int, toPosition: Int) {
        Collections.swap(
            uRIsContainDuplicatesPriorityList,
            fromPosition,
            toPosition
        )
    }

    fun getURIsPrioritySet(foundDuplicatesList: List<DuplicateWithHighlightedLine>) {
        uRIsContainDuplicatesPriorityList =
            getFoldersURIsContainDuplicatesListUseCase.execute(foundDuplicatesList)
    }

    fun setDuplicatesHighlightedLinesByPriorityList(foundDuplicatesList: List<DuplicateWithHighlightedLine>) {
        setDuplicatesHighlightedLinesByPriorityListUseCase.execute(
            foundDuplicatesList,
            uRIsContainDuplicatesPriorityList
        )
    }
}