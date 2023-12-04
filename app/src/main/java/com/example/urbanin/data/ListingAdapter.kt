package com.example.urbanin.data

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class ListingAdapter(
    private val listingList: MutableList<ListingData>,
    private val context: Context?,
    private val mode: String,
    private val handler: Callbacks
) : RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    private lateinit var contextViewGroup: ViewGroup
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListingViewHolder {
        contextViewGroup = parent
        val itemView = when(mode) {
            "tenant" -> LayoutInflater.from(parent.context).inflate(R.layout.listing_card, parent, false)
            "landlord" -> LayoutInflater.from(parent.context).inflate(R.layout.landlord_listing_card, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.listing_card, parent, false)
        }
        return ListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listingList.size
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val imgResource = listingList[position].img
        Picasso.get().load(imgResource[0]).into(holder.listingImageView)
        holder.listingTitleView.text = listingList[position].title
        holder.listingDescriptionView.text = formatSubTitle(listingList[position].numRooms,listingList[position].numBaths, listingList[position].availableFrom)
        holder.listingAddressView.text = listingList[position].address

        when(mode) {
            "tenant" -> {
                // on click listener for each item in the recycler view
                holder.itemView.setOnClickListener {
                    Log.d(TAG, "Position: $position -> ${listingList[position]}")
//                    Navigation.createNavigateOnClickListener(R.id.navigate_to_detailed_listing_fragment).onClick(holder.listingImageView)
                    handler.handleListingData(listingList[position], mode)
                }
                // save a listing
                val favButton = holder.itemView.findViewById<ImageButton>(R.id.listingItemSave)
                favButton.setOnClickListener {
                    if (listingList[position] in savedCollection) {
                        savedCollection.remove(listingList[position])
                        favButton.setImageResource(R.drawable.listing_favorite_outline_24)
                    } else {
                        savedCollection.add(listingList[position])
                        favButton.setImageResource(R.drawable.listing_favorite_24)
                    }
                }
            }
            "landlord" -> {
                // on click listener for each item in the recycler view
                holder.itemView.setOnClickListener {
                    Log.d(TAG, "Position: $position -> ${listingList[position]}")
//                    Navigation.createNavigateOnClickListener(R.id.navigate_to_detailed_listing_fragment).onClick(holder.listingImageView)
                    handler.handleListingData(listingList[position], mode)
                }
                // edit listing
                holder.itemView.findViewById<ImageButton>(R.id.editListingButton).setOnClickListener {
                    handler.handleListingData(listingList[position], "edit")
                }
                // delete listing
                holder.itemView.findViewById<ImageButton>(R.id.deleteListingButton).setOnClickListener {
                    handler.handleListingData(listingList[position], "delete")
                }
            }
        }


    }

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listingImageView: ImageView = itemView.findViewById(R.id.listingItemImage)
        val listingTitleView: TextView = itemView.findViewById(R.id.listingItemPrice)
        val listingDescriptionView: TextView = itemView.findViewById(R.id.listingItemDescription)
        val listingAddressView: TextView = itemView.findViewById(R.id.listingItemAddress)
    }

    private fun formatSubTitle(bed: String, bath: String, from: String): String {
        val numBed = if(bed != "Studio") {
            "$bed bed"
        } else {
            bed
        }

        val availableFrom = if(compareDate(from)) {
            "Available Now"
        } else {
            "Available from $from"
        }

        return "$numBed | $bath bath | $availableFrom"
    }

    private fun compareDate(date: String): Boolean {
        // get listing availableFrom date
        val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.US)
        val availableDate = dateFormat.parse(date)
        // get current date
        val currentDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val currentDate = currentDateFormat.parse(LocalDate.now().toString())

        return currentDate!! > availableDate
    }
    interface  Callbacks {
        fun handleListingData(data: ListingData, flag: String) {

        }
    }
}