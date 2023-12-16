package com.drexask.reduplicate

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.storagetools.StorageFile

class DuplicateLinesAdapter(
    context: Context,
    private var duplicateLinesList: List<StorageFile>
): RecyclerView.Adapter<DuplicateLinesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getItemCount(): Int = duplicateLinesList.size

    private fun getItem(position: Int): StorageFile = duplicateLinesList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuplicateLinesViewHolder {
        return DuplicateLinesViewHolder(inflater.inflate(R.layout.list_item_duplicate_line, parent, false))
    }

    override fun onBindViewHolder(holder: DuplicateLinesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}