package com.drexask.reduplicate.domain.models

import com.drexask.reduplicate.storagetools.Storable

class Duplicates(val DuplicatesMap: Map<String, MutableList<Storable>>)