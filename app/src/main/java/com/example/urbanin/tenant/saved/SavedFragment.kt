package com.example.urbanin.tenant.saved

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.auth.LoginPreferenceManager
import com.example.urbanin.databinding.FragmentSavedBinding
import com.example.urbanin.tenant.search.ListingAdapter
import com.example.urbanin.tenant.search.ListingData
import com.example.urbanin.tenant.search.SearchFragment.Companion.showSaved
import com.example.urbanin.tenant.search.savedCollection
import com.example.urbanin.tenant.search.savedSelectedOption
import com.example.urbanin.tenant.search.savedSortBy
import com.example.urbanin.tenant.search.sortOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SavedFragment : Fragment(), ListingAdapter.Callbacks {

    private lateinit var binding: FragmentSavedBinding

    private lateinit var listingRecyclerView: RecyclerView

    private lateinit var prefManager: LoginPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSavedBinding.inflate(layoutInflater)

        // TODO: temp toggling views, needs to be based on whether user is logged in
        prefManager = LoginPreferenceManager(requireContext())

        if(showSaved) {
            binding.loggedInView.visibility = View.VISIBLE
            setupRecyclerView()

            // sort listings
            binding.saveListingSort.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sort By")
                    .setSingleChoiceItems(sortOptions, savedSelectedOption) { _, which ->
                        savedSelectedOption = which
                    }
                    .setPositiveButton("Show Results") { _, _ ->
                        savedSortBy = sortOptions[savedSelectedOption]
                        sortListings()
                        setupRecyclerView()
                    }
                    .setNeutralButton("Cancel"){ _, _ ->
                        savedSelectedOption = sortOptions.indexOf(savedSortBy)
                    }
                    .show()
            }
        } else {
            binding.loggedInView.visibility = View.GONE
            binding.loggedOutView.visibility = View.VISIBLE
        }

        binding.saveListingLoginBtn.setOnClickListener {
            showSaved = true
            binding.loggedInView.visibility = View.VISIBLE
            binding.loggedOutView.visibility = View.GONE

            setupRecyclerView()

            // sort listings
            binding.saveListingSort.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sort By")
                    .setSingleChoiceItems(sortOptions, savedSelectedOption) { _, which ->
                        savedSelectedOption = which
                    }
                    .setPositiveButton("Show Results") { _, _ ->
                        savedSortBy = sortOptions[savedSelectedOption]
                        sortListings()
                        setupRecyclerView()
                    }
                    .setNeutralButton("Cancel"){ _, _ ->
                        savedSelectedOption = sortOptions.indexOf(savedSortBy)
                    }
                    .show()
            }
        }
    }

    private fun setupRecyclerView() {
        // Moving data into recycler view
        listingRecyclerView = binding.saveListingRecyclerView
        listingRecyclerView.setHasFixedSize(true)
        listingRecyclerView.layoutManager = LinearLayoutManager(context)
        listingRecyclerView.adapter = ListingAdapter(savedCollection, context,this)
    }

    private fun sortListings() {
        val roomComparator = mutableListOf("Studio", "1", "2", "3", "4", "5")
        val bathComparator = mutableListOf("1", "1.5", "2", "3", "4")

        savedCollection = when(savedSortBy) {
            "Latest" -> savedCollection.sortedWith(compareBy { it.datePosted }) as MutableList<ListingData>
            "Rent: Low to High" -> savedCollection.sortedWith(compareBy { it.price.toLong() }) as MutableList<ListingData>
            "Rent: High to Low" -> savedCollection.sortedWith(compareByDescending { it.price.toLong() }) as MutableList<ListingData>
            "Number of Rooms" -> savedCollection.sortedWith(compareBy { roomComparator.indexOf(it.numRooms) }) as MutableList<ListingData>
            "Number of Baths" -> savedCollection.sortedWith(compareBy { bathComparator.indexOf(it.numBaths) }) as MutableList<ListingData>
            else -> savedCollection
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_saved, container, false)
        return binding.root
    }

}