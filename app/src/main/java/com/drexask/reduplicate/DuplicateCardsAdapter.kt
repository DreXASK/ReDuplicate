package com.drexask.reduplicate

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.drexask.reduplicate.domain.models.Duplicate


class DuplicateCardsAdapter(
    val context: Context,
    private var duplicatesList: List<Duplicate>
): RecyclerView.Adapter<DuplicateCardsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getItemCount(): Int = duplicatesList.size

    private fun getItem(position: Int): Duplicate = duplicatesList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuplicateCardsViewHolder {
        return DuplicateCardsViewHolder(inflater.inflate(R.layout.list_item_duplicates_card, parent, false))
    }

    override fun onBindViewHolder(holder: DuplicateCardsViewHolder, position: Int) {
        holder.bind(context, getItem(position))
    }

}
