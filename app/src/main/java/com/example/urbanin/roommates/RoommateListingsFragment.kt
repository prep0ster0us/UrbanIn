package com.example.urbanin.roommates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R


class RoommateListingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoommateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_roommate_listings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerViewRoommates)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch data from your database and initialize the adapter
        val sampleListings = getSampleRoommateListings()
        adapter = RoommateAdapter(sampleListings)
        recyclerView.adapter = adapter

        // Setup click listeners for TextView options
        setupOptionClickListeners(view)
    }

    private fun getSampleRoommateListings(): List<RoommateListings.RoommateListing> {
        // Fetch data from your database
        // Returning an empty list as a placeholder
        return listOf()
    }

    private fun setupOptionClickListeners(view: View) {
        view.findViewById<TextView>(R.id.textViewViewOption).setOnClickListener {
            // Handle View option click
        }
        view.findViewById<TextView>(R.id.textViewSortOption).setOnClickListener {
            // Handle Sort option click
        }
        view.findViewById<TextView>(R.id.textViewFilterOption).setOnClickListener {
            // Handle Filter option click
        }
    }
}
