package com.drexask.reduplicate.duplicatePrioritySelector.presentation

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.databinding.ListItemDuplicateCardBinding
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine

class DuplicateCardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    internal var binding: ListItemDuplicateCardBinding = ListItemDuplicateCardBinding.bind(view)

    fun bind(
        context: Context,
        duplicate: DuplicateWithHighlightedLine
    ) {
        binding.tvFileNameOrWeight.text = duplicate.duplicatesSharedParameters

        val duplicateLinesAdapter = DuplicateLinesAdapter(context, duplicate)
        binding.rvDuplicateLines.apply {
            adapter = duplicateLinesAdapter
            setHasFixedSize(true)
            visibility = if(duplicate.collapsed) View.GONE else View.VISIBLE
        }
    }
}