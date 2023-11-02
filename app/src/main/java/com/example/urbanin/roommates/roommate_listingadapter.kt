package com.example.urbanin.roommates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R

class RoommateListingAdapter(private val listingList: List<ListingCard>) :
    RecyclerView.Adapter<RoommateListingAdapter.ListingViewHolder>() {

    class ListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val ageTextView: TextView = view.findViewById(R.id.textViewAge)
        val professionTextView: TextView = view.findViewById(R.id.textViewProfession)
        val descriptionTextView: TextView = view.findViewById(R.id.textViewDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.roommate_listing_card, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val currentListing = listingList[position]
        holder.nameTextView.text = "Name: ${currentListing.name}"
        holder.ageTextView.text = "Age: ${currentListing.age}"
        holder.professionTextView.text = "Profession: ${currentListing.profession}"
        holder.descriptionTextView.text = "Description: ${currentListing.description}"
    }

    override fun getItemCount(): Int = listingList.size
}
