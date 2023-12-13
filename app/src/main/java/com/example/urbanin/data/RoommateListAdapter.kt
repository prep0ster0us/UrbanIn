package com.example.urbanin.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R
import com.example.urbanin.roommates.RoommateAdapter
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.NumberFormat

class RoommateListAdapter(
    private val options: FirestoreRecyclerOptions<RoommateListingData>,
    private val context: Context?,
    private val handler: Callbacks
) : FirestoreRecyclerAdapter<RoommateListingData, RoommateListAdapter.RoommateListViewHolder>(options) {

    private lateinit var contextViewGroup: ViewGroup
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoommateListViewHolder {
        contextViewGroup = parent
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.roommate_listing_card, parent, false)

        return RoommateListViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: RoommateListViewHolder,
        position: Int,
        model: RoommateListingData
    ) {
        with(holder) {
            if(model.img.isNotEmpty()) {
                Picasso.get().load(model.img).into(rmImg)
                rmImg.scaleType=ImageView.ScaleType.CENTER_CROP
            } else {
                rmImg.scaleType=ImageView.ScaleType.FIT_CENTER
            }
            rmTitle.text = "${model.name} (${model.age})"
            rmShortDescription.text = if(model.gender != "") {
                "${model.occupation}, ${model.gender}\n${model.roomSize} room"
            } else {
                "${model.occupation}\n${model.roomSize} room"
            }
            rmBudget.text = "${formatAsCurrency(model.budget.toFloat())}/month"
            rmMoveIn.text = model.moveInDate

            // on click listener for each item in the recycler view
            itemView.setOnClickListener {
                handler.handleListingData(model)
            }
        }
    }

    private fun formatAsCurrency(value: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return "$ " + formatter.format(value).toString()
    }

    class RoommateListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rmImg: ImageView = itemView.findViewById(R.id.rmImage)
        val rmTitle: TextView = itemView.findViewById(R.id.rmHeader)
        val rmShortDescription: TextView = itemView.findViewById(R.id.rmSubHeader)
        val rmBudget: TextView = itemView.findViewById(R.id.rmBudget)
        val rmMoveIn: TextView = itemView.findViewById(R.id.rmDate)
    }

    interface  Callbacks {
        fun handleListingData(data: RoommateListingData) {

        }
    }
}