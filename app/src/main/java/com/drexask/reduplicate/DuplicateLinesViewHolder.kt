package com.drexask.reduplicate

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.databinding.ListItemDuplicateLineBinding
import com.drexask.reduplicate.storagetools.StorageFile

class DuplicateLinesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    var binding: ListItemDuplicateLineBinding = ListItemDuplicateLineBinding.bind(view)
    fun bind(storageFile: StorageFile, isHighlighted: Boolean) {
        binding.apply {
            tvDuplicateUri.text = storageFile.file.uri.lastPathSegment?.replaceFirst("primary:", "")
            cardView.isActivated = isHighlighted
        }
    }
}