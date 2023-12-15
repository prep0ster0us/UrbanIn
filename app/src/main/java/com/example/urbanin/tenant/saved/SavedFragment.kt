package com.example.urbanin.tenant.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R
import com.example.urbanin.data.ListingAdapter
import com.example.urbanin.data.ListingData
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.SortParameters
import com.example.urbanin.data.savedCollection
import com.example.urbanin.data.sortOptions
import com.example.urbanin.databinding.FragmentSavedBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SavedFragment : Fragment(), ListingAdapter.Callbacks {

    // TODO: <known flaw> saved listings are not stored in tandom with the user logged in, added from listings page locally

    private lateinit var binding: FragmentSavedBinding

    private lateinit var listingRecyclerView: RecyclerView

    private lateinit var prefManager: LoginPreferenceManager

    // for sorting
    private var sortParams: SortParameters = SortParameters()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSavedBinding.inflate(layoutInflater)

        prefManager = LoginPreferenceManager(requireContext())

        if (prefManager.isLoggedIn()) {
            binding.loggedOutView.visibility = View.GONE

            if(savedCollection.size == 0) {
                binding.loggedInView.visibility = View.GONE
                binding.emptyLayout.visibility = View.VISIBLE
            } else {
                binding.loggedInView.visibility = View.VISIBLE
                binding.emptyLayout.visibility = View.GONE

                setupRecyclerView()

                // sort listings
                binding.saveListingSort.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogPalette)
                        .setTitle("Sort By")
                        .setSingleChoiceItems(sortOptions, sortParams.selectedOption) { _, which ->
                            sortParams.selectedOption = which
                        }
                        .setPositiveButton("Show Results") { _, _ ->
                            sortParams.sortBy = sortOptions[sortParams.selectedOption]
                            sortListings()
                            setupRecyclerView()
                        }
                        .setNeutralButton("Cancel") { _, _ ->
                            sortParams.selectedOption = sortOptions.indexOf(sortParams.sortBy)
                        }
                        .show()
                }
            }
        } else {
            binding.loggedOutView.visibility = View.VISIBLE
            binding.loggedInView.visibility = View.GONE
            binding.emptyLayout.visibility = View.GONE

            // redirect to login page
            binding.saveListingLoginBtn.setOnClickListener {
                prefManager.setRedirectContext("Saved")
                findNavController().navigate(SavedFragmentDirections.navigateSavedToLogin())
            }
        }

    }

    private fun setupRecyclerView() {
        // Moving data into recycler view
        listingRecyclerView = binding.saveListingRecyclerView
        listingRecyclerView.setHasFixedSize(true)
        listingRecyclerView.layoutManager = LinearLayoutManager(context)
        listingRecyclerView.adapter = ListingAdapter(savedCollection, context, "saved", this)
    }

    private fun sortListings() {
        val roomComparator = mutableListOf("Studio", "1", "2", "3", "4", "5")
        val bathComparator = mutableListOf("1", "1.5", "2", "3", "4")

        savedCollection = when (sortParams.sortBy) {
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
        return binding.root
    }

}