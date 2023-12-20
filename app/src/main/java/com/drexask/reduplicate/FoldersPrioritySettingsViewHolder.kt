package com.drexask.reduplicate

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.databinding.ListItemFolderUriPriorityLineBinding

class FoldersPrioritySettingsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private var binding: ListItemFolderUriPriorityLineBinding = ListItemFolderUriPriorityLineBinding.bind(view)
    fun bind(folderUri: Uri) {
        binding.apply {
            tvFolderUri.text = folderUri.lastPathSegment?.replaceFirst("primary:", "")
            tvPosition.text = layoutPosition.toString()
        }
    }
}