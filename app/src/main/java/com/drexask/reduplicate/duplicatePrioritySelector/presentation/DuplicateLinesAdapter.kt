package com.drexask.reduplicate.duplicatePrioritySelector.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.R
import com.drexask.reduplicate.core.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.duplicateFinder.domain.models.StorageFile

class DuplicateLinesAdapter(
    val context: Context,
    private var duplicateWithHighlightedLine: DuplicateWithHighlightedLine
): RecyclerView.Adapter<DuplicateLinesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getItemCount(): Int = duplicateWithHighlightedLine.duplicateFilesInnerList.size

    private fun getItem(position: Int): StorageFile = duplicateWithHighlightedLine.duplicateFilesInnerList[position]

    private fun getHighlightedPropertyValue(position: Int): Boolean = position == duplicateWithHighlightedLine.highlightedLineIndex

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuplicateLinesViewHolder {
        return DuplicateLinesViewHolder(inflater.inflate(R.layout.list_item_duplicate_line, parent, false))
    }

    @SuppressLint("NotifyDataSetChanged") // Must to refresh all data set
    override fun onBindViewHolder(holder: DuplicateLinesViewHolder, position: Int) {
        holder.bind(getItem(position), getHighlightedPropertyValue(position))

        holder.binding.apply {
            cardView.setOnClickListener {
                duplicateWithHighlightedLine.highlightedLineIndex = position
                notifyDataSetChanged()
            }
        }

    }
}