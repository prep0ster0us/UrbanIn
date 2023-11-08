package com.example.urbanin.landlord

import LandlordListingAdapter
import ListingItemListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentLandlordBinding
import com.example.urbanin.tenant.search.ListingAdapter
import com.example.urbanin.tenant.search.ListingCard
import com.example.urbanin.tenant.search.ListingData
import com.example.urbanin.tenant.search.listingCollection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LandlordFragment : Fragment(), ListingItemListener {

    private var _binding: FragmentLandlordBinding? = null
    private val binding get() = _binding!!

    private var db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var adapter: LandlordListingAdapter
    private var userListings: ArrayList<ListingData> = arrayListOf()
    private var listings = ArrayList<ListingCard>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandlordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // TODO: Load your listings data into the 'listings' list
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = LandlordListingAdapter(listings, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        // Mock data for demonstration
//        listings.add(
//            ListingCard(
//                imgResource = "",
//                title = "Apartment",
//                description = "4 bds | 1.5 ba | 1,500 sqft",
//                location = "XX ABC St, New Haven, CT"
//            )
//        )
        // TODO: Load your actual data here
        getListingsFromDatabase()
        adapter.notifyDataSetChanged()
    }

    private fun getListingsFromDatabase() {
//        TODO("Not yet implemented")
        db.collection("Users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "data fetched for user: ${auth.currentUser!!.uid}")
                userListings = document.data!!["Listings"] as ArrayList<ListingData>
                for (listing in userListings) {
                    listings.add(
                        ListingCard(
                            listing.img,
                            listing.title,
                            listing.description,
                            listing.address
                        )
                    )
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "data fetch FAILED: $it")
            }
    }

    override fun onEditClicked(listing: LandlordListingCard.Listing) {
        // Handle edit action
        // For example, you can navigate to another fragment or activity for editing the listing
        println("Edit clicked for listing: ${listing.listingID}")
    }

    override fun onDeleteClicked(listing: LandlordListingCard.Listing) {
        // Handle delete action
        // For example, you can show a confirmation dialog, and if confirmed, delete the listing from the database and update the RecyclerView
        println("Delete clicked for listing: ${listing.listingID}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
