package com.drexask.reduplicate.folderPrioritySettings.presentation

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.databinding.ListItemFolderUriPriorityLineBinding

class FoldersPrioritySettingsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private var binding: ListItemFolderUriPriorityLineBinding = ListItemFolderUriPriorityLineBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(folderUri: Uri) {
        binding.apply {
            tvPosition.text = (layoutPosition + 1).toString()
            tvFolderUri.text = folderUri.lastPathSegment?.replaceFirst("primary:", "")
        }
    }
}