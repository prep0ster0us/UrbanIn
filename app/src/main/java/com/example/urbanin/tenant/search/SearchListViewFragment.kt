package com.example.urbanin.tenant.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentSearchListViewBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class SearchListViewFragment : Fragment() {
    private lateinit var binding: FragmentSearchListViewBinding

    private lateinit var listingRecyclerView: RecyclerView

    private var db = FirebaseFirestore.getInstance()

//    private val args:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(requireContext())
        getListingFromFirebase()

        binding = FragmentSearchListViewBinding.inflate(layoutInflater)

        // Recycler View
        val listingRecyclerList: ArrayList<ListingCard> = ArrayList()
        for (listing in listingCollection) {
            listingRecyclerList.add(
                ListingCard(
                    listing.img,
                    listing.title,
                    listing.description,
                    listing.location
                )
            )
        }

        // Moving data into recycler view
        listingRecyclerView = binding.searchListingListView
        listingRecyclerView.setHasFixedSize(true)
        listingRecyclerView.layoutManager = LinearLayoutManager(context)
        listingRecyclerView.adapter = ListingAdapter(listingRecyclerList, this)

    }

    private fun getListingFromFirebase() {
        db.collection("Listings")
            .get()
            .addOnSuccessListener { documents ->
                for(doc in documents) {
                    Log.d(TAG, "Document ${doc.id} => ${doc.data}")

                    var checkExisting = false
                    for(listing in listingCollection) {
                        if(listing.listingID == doc.id) {
                            checkExisting = true
                            break
                        }
                    }
                    Log.d(TAG, "${doc.data["utilities"]}")
                    Log.d(TAG, "${doc.data["amenities"]}")
                    if(!checkExisting) {
                        listingCollection.add(
                            ListingData(
                                doc.id,
                                doc.data["userID"] as String,
                                doc.data["type"] as String,
                                doc.data["title"] as String,
                                doc.data["description"] as String,
                                doc.data["location"] as String,
                                doc.data["price"] as Long,
                                doc.data["img"] as String,
                                doc.data["datePosted"] as String,
                                doc.data["availableFrom"] as String,
                                doc.data["numRooms"] as Long,
                                doc.data["numBaths"] as Long,
                                doc.data["petsAllowed"] as String,
                                doc.data["utilities"] as Map<String, Boolean>,
                                doc.data["amenities"] as Map<String, Boolean>
                            )
                        )
                    }
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Error getting data!")
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