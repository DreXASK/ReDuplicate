package com.drexask.reduplicate.domain.usecases

import android.content.Intent
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultLauncher

class ChooseFolderUseCase (
    private val activityFolderPickerResultLauncher: ActivityResultLauncher<Intent>
) {

    fun execute() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            .putExtra(
                DocumentsContract.EXTRA_INITIAL_URI,
                Environment.getExternalStorageDirectory()
            )
        activityFolderPickerResultLauncher.launch(intent)
    }

}