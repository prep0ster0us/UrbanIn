package com.example.urbanin.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R

class FacilityAdapter(
    private val context: Context,
    private val gridList: ArrayList<FacilityItem>
) : RecyclerView.Adapter<FacilityAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.facility_card_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return gridList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.facilityIcon.setImageResource(gridList[position].imgResource)
        holder.facilityText.text = gridList[position].itemText
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val facilityIcon: ImageView = itemView.findViewById(R.id.cardImage)
        val facilityText: TextView = itemView.findViewById(R.id.cardText)
    }
}