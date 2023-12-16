package com.drexask.reduplicate

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.databinding.ListItemDuplicatesCardBinding
import com.drexask.reduplicate.domain.models.Duplicate

class DuplicateCardsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private var binding: ListItemDuplicatesCardBinding = ListItemDuplicatesCardBinding.bind(view)

    fun bind(context: Context, duplicate: Duplicate) {
        binding.tvFileNameOrWeight.text = duplicate.duplicatesSharedParameters

        val duplicateLinesAdapter = DuplicateLinesAdapter(context, duplicate.duplicateFilesInnerList)
        binding.rvDuplicateUri.apply {
            adapter = duplicateLinesAdapter
            setHasFixedSize(true)
        }
    }
}