package com.drexask.reduplicate.duplicatePrioritySelector.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.R
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine


class DuplicateCardsAdapter(
    val context: Context,
    private var duplicatesList: List<DuplicateWithHighlightedLine>
) : RecyclerView.Adapter<DuplicateCardsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = duplicatesList.size

    private fun getItem(position: Int): DuplicateWithHighlightedLine = duplicatesList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuplicateCardsViewHolder {
        return DuplicateCardsViewHolder(
            inflater.inflate(
                R.layout.list_item_duplicate_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DuplicateCardsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(context, item)

        holder.binding.apply {
            tvFileNameOrWeight.setOnClickListener {
                item.collapsed = !item.collapsed
                notifyItemChanged(position)
            }
        }

    }
}