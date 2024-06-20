package com.drexask.reduplicate.duplicateRemover.presentation

import javax.inject.Inject

class ConvertBytes @Inject constructor() {

    enum class InformationUnit(val ratioToBytes: Double) {
        Bytes(1.0),
        Kilobytes(1024.0),
        Megabytes(1048576.0),
        Gigabytes(1073741824.0)
    }

    fun execute(bytes: Long): Pair<Double, InformationUnit> {
        return when {
            (bytes / InformationUnit.Gigabytes.ratioToBytes > 1.0) -> Pair(
                bytes / InformationUnit.Gigabytes.ratioToBytes,
                InformationUnit.Gigabytes
            )

            (bytes / InformationUnit.Megabytes.ratioToBytes > 1.0) -> Pair(
                bytes / InformationUnit.Megabytes.ratioToBytes,
                InformationUnit.Megabytes
            )

            (bytes / InformationUnit.Kilobytes.ratioToBytes > 1.0) -> Pair(
                bytes / InformationUnit.Kilobytes.ratioToBytes,
                InformationUnit.Kilobytes
            )

            else -> Pair(bytes.toDouble(), InformationUnit.Bytes)
        }
    }
}