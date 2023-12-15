package com.drexask.reduplicate.domain.models

import androidx.lifecycle.MutableLiveData

data class DuplicatesFindSettings(
    val useFileNames: Boolean,
    val useFileWeights: Boolean
)