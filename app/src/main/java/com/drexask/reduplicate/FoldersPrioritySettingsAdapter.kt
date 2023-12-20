package com.drexask.reduplicate

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FoldersPrioritySettingsAdapter(
    val context: Context,
    private var uRIsContainDuplicatesPriorityList: MutableList<Uri>
) : RecyclerView.Adapter<FoldersPrioritySettingsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = uRIsContainDuplicatesPriorityList.size

    private fun getItem(position: Int): Uri = uRIsContainDuplicatesPriorityList[position]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoldersPrioritySettingsViewHolder {
        return FoldersPrioritySettingsViewHolder(
            inflater.inflate(
                R.layout.list_item_folder_uri_priority_line,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FoldersPrioritySettingsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
