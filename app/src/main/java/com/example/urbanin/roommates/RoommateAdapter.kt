package com.example.urbanin.roommates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.urbanin.R

class RoommateAdapter(private val listings: List<RoommateListings.RoommateListing>) : RecyclerView.Adapter<RoommateAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val imageView: ImageView = view.findViewById(R.id.image_roommate)
//        val textViewLocation: TextView = view.findViewById(R.id.text_location)
        // Add other UI components as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.roommate_listing_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = listings[position]
//        holder.textViewLocation.text = listing.location

        if (listing.images.isNotEmpty()) {
//            Glide.with(holder.imageView.context).load(listing.images[0]).into(holder.imageView)
        }

        // Bind other details as needed
    }

    override fun getItemCount() = listings.size
}
