package com.example.urbanin.tenant.search.Amenities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.urbanin.R

class AmenitiesAdapter(
    private val context: Context,
    private val cardList: ArrayList<AmenitiesCard>
) : BaseAdapter() {

    private var inflater: LayoutInflater? = null
    private lateinit var cardImage: ImageView
    private lateinit var cardText: TextView

    override fun getCount(): Int {
        return cardList.count()
    }

    // used to return the item of the grid view
    override fun getItem(p0: Int): Any? {
        return null
    }

    // used to return the item id of the grid view
    override fun getItemId(p0: Int): Long {
        return 0
    }

    // get individual item of grid view.
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var convertView = p1

        // on blow line we are checking
        // if layout inflate is null, if it is null we are initializing it.
        if (inflater == null) {
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        // on the below line we are checking if convert view is null.
        // If it is null we are initializing it.
        if (convertView == null) {
            // on below line we are passing the layout file
            // which we have to inflate for each item of grid view.
            convertView = inflater!!.inflate(R.layout.amenities_card, null)
        }

        cardImage = convertView!!.findViewById(R.id.cardImage)
        cardImage.setImageResource(cardList[p0].imgResource)

        cardText = convertView.findViewById(R.id.cardText)
        cardText.text = cardList[p0].itemText

        return convertView
    }
}