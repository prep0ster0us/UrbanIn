package com.example.urbanin.tenant.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R

class ListingAdapter(
    private val listingList: ArrayList<ListingCard>,
    private val context: SearchListViewFragment,
) : RecyclerView.Adapter<ListingAdapter.listingViewHolder>() {

    private lateinit var contextViewGroup: ViewGroup
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): listingViewHolder {
        contextViewGroup = parent
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.listing_card, parent, false)
        return listingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listingList.size
    }

    override fun onBindViewHolder(holder: listingViewHolder, position: Int) {
        val (imgResource, title, description, location) = listingList[position]
        // TODO: pass ImageView in "imgResource" var, which can be set for each card view (or each listing)
//        holder.listingImageView.setImageResource(imgResource)
        holder.listingTitleView.text = title
        holder.listingDescriptionView.text = description
        holder.listingAddressView.text = location

        // on click listener for each item in the recycler view
        holder.itemView.setOnClickListener {
            Toast.makeText(
                contextViewGroup.context,
                "Clicked on item num-$position",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    class listingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listingImageView: ImageView = itemView.findViewById(R.id.listingItemImage)
        val listingTitleView: TextView = itemView.findViewById(R.id.listingItemPrice)
        val listingDescriptionView: TextView = itemView.findViewById(R.id.listingItemDescription)
        val listingAddressView: TextView = itemView.findViewById(R.id.listingItemAddress)
    }
}