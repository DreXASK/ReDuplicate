package com.drexask.reduplicate

import android.net.Uri

fun Uri.removeFileFromUri() : Uri {
    val uriStringSegments = this.toString().split("%2F")

    val uriStringSegmentsWithoutFile =
        uriStringSegments.subList(0, uriStringSegments.size - 1) // exclusive end value

    val uriStringWithoutFile = uriStringSegmentsWithoutFile.joinToString("%2F")

    return Uri.parse(uriStringWithoutFile)
}