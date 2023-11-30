package com.example.urbanin.tenant.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentSearchListViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class SearchListViewFragment : Fragment(), ListingAdapter.Callbacks {
    private lateinit var binding: FragmentSearchListViewBinding

    private lateinit var listingRecyclerView: RecyclerView

    private var db = FirebaseFirestore.getInstance()

//    private val args:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchListViewBinding.inflate(layoutInflater)

        setupRecyclerView()

        // sort listings
        binding.searchListViewSort.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort By")
                .setSingleChoiceItems(sortOptions, selectedOption) { _, which ->
                    selectedOption = which
                }
                .setPositiveButton("Show Results") { _, _ ->
                    sortBy = sortOptions[selectedOption]
                    sortListings()
                    setupRecyclerView()
                }
                .setNeutralButton("Cancel"){ _, _ ->
                    selectedOption = sortOptions.indexOf(sortBy)
                }
                .show()
        }

    }

    private fun setupRecyclerView() {
        // Moving data into recycler view
        listingRecyclerView = binding.searchListingListView
        listingRecyclerView.setHasFixedSize(true)
        listingRecyclerView.layoutManager = LinearLayoutManager(context)
        listingRecyclerView.adapter = ListingAdapter(listingCollection, context,this)
    }


    private fun sortListings() {
        val roomComparator = mutableListOf("Studio", "1", "2", "3", "4", "5")
        val bathComparator = mutableListOf("1", "1.5", "2", "3", "4")

        listingCollection = when(sortBy) {
            "Latest" -> listingCollection.sortedWith(compareBy { it.datePosted }) as MutableList<ListingData>
            "Rent: Low to High" -> listingCollection.sortedWith(compareBy { it.price.toLong() }) as MutableList<ListingData>
            "Rent: High to Low" -> listingCollection.sortedWith(compareByDescending { it.price.toLong() }) as MutableList<ListingData>
            "Number of Rooms" -> listingCollection.sortedWith(compareBy { roomComparator.indexOf(it.numRooms) }) as MutableList<ListingData>
            "Number of Baths" -> listingCollection.sortedWith(compareBy { bathComparator.indexOf(it.numBaths) }) as MutableList<ListingData>
            else -> listingCollection
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun handleListingData(data: ListingData) {
        val action = SearchListViewFragmentDirections.navigateToDetailedListingFragment(data)
        findNavController().navigate(action)
    }
}