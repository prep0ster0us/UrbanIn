package com.example.urbanin.roommates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.databinding.FragmentSearchListViewBinding

class RoommateSearchListViewFragment : Fragment() {

    private var _binding: FragmentSearchListViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var listingRecyclerView: RecyclerView
    private lateinit var listingAdapter: roommate_listingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchListViewBinding.inflate(layoutInflater)

        // Initialize the data source for the listings
        val listingCollection: List<ListingCard> = initializeListingCollection()

        // Initialize the adapter and assign it to the RecyclerView
        listingAdapter = roommate_listingAdapter(listingCollection)
        listingRecyclerView = binding.searchListingListView
        listingRecyclerView.adapter = listingAdapter
        listingRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun initializeListingCollection(): List<ListingCard> {
        // Replace with actual data fetching logic
        return listOf(
            ListingCard("John Doe", 25, "Software Developer", "Looking for a clean and friendly roommate. Non-smoker preferred."),
            // Add more listings as needed
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

