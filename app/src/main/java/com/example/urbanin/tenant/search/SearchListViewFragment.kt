package com.example.urbanin.tenant.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity
import com.example.urbanin.R
import com.example.urbanin.data.ListingAdapter
import com.example.urbanin.data.ListingData
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.data.SortParameters
import com.example.urbanin.data.listingCollection
import com.example.urbanin.data.sortOptions
import com.example.urbanin.databinding.FragmentSearchListViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class SearchListViewFragment : Fragment(), ListingAdapter.Callbacks {
    private lateinit var binding: FragmentSearchListViewBinding

    private lateinit var listingRecyclerView: RecyclerView

    private var db = FirebaseFirestore.getInstance()

    // for sorting
    private var sortParams = SortParameters()

//    private val args:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchListViewBinding.inflate(layoutInflater)

        setupRecyclerView()

        // sort listings
        binding.searchListViewSort.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort By")
                .setSingleChoiceItems(sortOptions, sortParams.selectedOption) { _, which ->
                    sortParams.selectedOption = which
                }
                .setPositiveButton("Show Results") { _, _ ->
                    sortParams.sortBy = sortOptions[sortParams.selectedOption]
                    sortListings()
                    setupRecyclerView()
                }
                .setNeutralButton("Cancel"){ _, _ ->
                    sortParams.selectedOption = sortOptions.indexOf(sortParams.sortBy)
                }
                .show()
        }
//        with(SearchListingUtil) {
//            setTenantNavBarVisibility(requireActivity(), true)
//            setSearchBarVisibility(requireActivity(), true)
//        }
    }

    private fun setupRecyclerView() {
        // Moving data into recycler view
        listingRecyclerView = binding.searchListingListView
        listingRecyclerView.setHasFixedSize(true)
        listingRecyclerView.layoutManager = LinearLayoutManager(context)
        listingRecyclerView.adapter = ListingAdapter(listingCollection, context, "tenant", this)
    }


    private fun sortListings() {
        val roomComparator = mutableListOf("Studio", "1", "2", "3", "4", "5")
        val bathComparator = mutableListOf("1", "1.5", "2", "3", "4")

        when(sortParams.sortBy) {
            "Latest" -> listingCollection.sortBy { it.datePosted }
            "Rent: Low to High" -> listingCollection.sortBy { it.price.toLong() }
            "Rent: High to Low" -> listingCollection.sortByDescending { it.price.toLong() }
            "Number of Rooms" -> listingCollection.sortBy { roomComparator.indexOf(it.numRooms) }
            "Number of Baths" -> listingCollection.sortBy { bathComparator.indexOf(it.numBaths) }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SearchListingUtil) {
            setTenantNavBarVisibility(requireActivity(), true)
            setSearchBarVisibility(requireActivity(), true)
        }
    }

    override fun handleListingData(data: ListingData, flag: String) {
        val action = SearchListViewFragmentDirections.navigateToDetailedListingFragmentFromList(data)
        findNavController().navigate(action)
    }
}