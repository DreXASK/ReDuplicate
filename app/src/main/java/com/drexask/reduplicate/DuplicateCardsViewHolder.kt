package com.drexask.reduplicate

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.databinding.ListItemDuplicateCardBinding
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine

class DuplicateCardsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private var binding: ListItemDuplicateCardBinding = ListItemDuplicateCardBinding.bind(view)

    fun bind(context: Context, duplicate: DuplicateWithHighlightedLine) {
        binding.tvFileNameOrWeight.text = duplicate.duplicatesSharedParameters

        val duplicateLinesAdapter = DuplicateLinesAdapter(context, duplicate)
        binding.rvDuplicateUri.apply {
            adapter = duplicateLinesAdapter
            setHasFixedSize(true)
        }
    }
}