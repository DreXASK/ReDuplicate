package com.drexask.reduplicate

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DuplicateFinderFragmentViewModel @Inject constructor(): ViewModel() {
    val treeUri = MutableLiveData<Uri>()
}