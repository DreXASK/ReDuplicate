package com.drexask.reduplicate

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class MainActivitySharedData @Inject constructor () {
    var foundDuplicatesList: List<DuplicateWithHighlightedLine>? = null
    var uRIsContainDuplicatesPriorityList: MutableList<Uri>? = null

    val useFileNamesLD = MutableLiveData<Boolean>().apply { value = true }
    val useFileWeightsLD = MutableLiveData<Boolean>().apply { value = false }

    fun clearAllData() {
        foundDuplicatesList = null
        uRIsContainDuplicatesPriorityList?.clear()
    }
}
