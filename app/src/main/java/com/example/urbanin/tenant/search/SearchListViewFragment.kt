package com.example.urbanin.tenant.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentSearchListViewBinding

class SearchListViewFragment : Fragment() {
    private lateinit var binding: FragmentSearchListViewBinding

    private lateinit var listingRecyclerView: RecyclerView

//    private val args:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }
}