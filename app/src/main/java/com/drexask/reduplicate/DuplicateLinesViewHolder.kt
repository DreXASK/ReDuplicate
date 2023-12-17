package com.drexask.reduplicate

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.databinding.ListItemDuplicateLineBinding
import com.drexask.reduplicate.storagetools.StorageFile

class DuplicateLinesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private var binding: ListItemDuplicateLineBinding = ListItemDuplicateLineBinding.bind(view)
    fun bind(storageFile: StorageFile, @ColorInt color: Int, buttonCallBack: (position: Int) -> Unit) {
        binding.apply {
            tvDuplicateUri.text = storageFile.file.uri.lastPathSegment
            cardView.setCardBackgroundColor(color)
            btnSelectThisDuplicateLine.setOnClickListener {
                buttonCallBack(layoutPosition)
            }
        }
    }
}