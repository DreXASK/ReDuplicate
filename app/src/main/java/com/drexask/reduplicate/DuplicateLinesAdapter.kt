package com.drexask.reduplicate

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.domain.models.DuplicateWithHighlightedLine
import com.drexask.reduplicate.storagetools.StorageFile

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

    override fun onBindViewHolder(holder: DuplicateLinesViewHolder, position: Int) {
        val color = if (getHighlightedPropertyValue(position)) Color.RED else Color.GRAY
        holder.bind(getItem(position), color, ::highlightLineByPosition)
    }

    @SuppressLint("NotifyDataSetChanged") // We need to refresh all set
    fun highlightLineByPosition(position: Int) {
        duplicateWithHighlightedLine.highlightedLineIndex = position
        notifyDataSetChanged()
    }

}