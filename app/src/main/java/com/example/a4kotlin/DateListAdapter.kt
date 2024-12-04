package com.example.a4kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DateListAdapter(
    private var photoPaths: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DateListAdapter.DateViewHolder>() {

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoInfoTextView: TextView = itemView.findViewById(R.id.photoInfoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val photoInfo = photoPaths[position]
        holder.photoInfoTextView.text = photoInfo

        holder.itemView.setOnClickListener {
            onItemClick(photoInfo)
        }
    }

    override fun getItemCount(): Int = photoPaths.size

    fun updatePhotoPaths(newPhotoPaths: List<String>) {
        photoPaths = newPhotoPaths
        notifyDataSetChanged()
    }
}
