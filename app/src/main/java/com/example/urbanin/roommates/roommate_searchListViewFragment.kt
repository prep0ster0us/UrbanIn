package com.example.urbanin.roommates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.databinding.FragmentSearchListViewBinding

class SearchListViewFragment : Fragment() {

    private var _binding: FragmentSearchListViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var listingAdapter: RoommateListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchListViewBinding.inflate(inflater, container, false)

        // Initialize the data source for the listings
        val listingCollection: List<ListingCard> = initializeListingCollection()

        // Initialize the adapter and assign it to the RecyclerView
        listingAdapter = RoommateListingAdapter(listingCollection)
        binding.searchListingListView.adapter = listingAdapter
        binding.searchListingListView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    private fun initializeListingCollection(): List<ListingCard> {
        // Replace with actual data fetching logic
        return listOf(
            ListingCard("John Doe", 25, "Software Developer", "Looking for a clean and friendly roommate. Non-smoker preferred."),
            // Add more listings as needed
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
