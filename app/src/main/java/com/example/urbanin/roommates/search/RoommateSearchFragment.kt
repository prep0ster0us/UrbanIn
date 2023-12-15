package com.example.urbanin.roommates.search

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
import com.bumptech.glide.Glide
import com.example.urbanin.R
import com.example.urbanin.data.RoommateListAdapter
import com.example.urbanin.data.RoommateListingData
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.FragmentRoommateSearchBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class RoommateSearchFragment : Fragment(), RoommateListAdapter.Callbacks {

    // TODO: <known flaw> roommate user mode always assumes user is logged in, will throw error if not (since doesn't pass through login page)
    private lateinit var binding: FragmentRoommateSearchBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoommateListAdapter

    // for sort
    private var sortOptions = arrayOf("Latest", "Budget", "Room Size", "Age", "Name")
    private var sortMap = linkedMapOf(
        "Latest" to "datePosted",
        "Budget" to "budget",
        "Room Size" to "roomSize",
        "Age" to "age",
        "Name" to "name"
    )
    private var selectedOption = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentRoommateSearchBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()

        binding.searchSort.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogPalette)
                .setTitle("Sort By")
                .setSingleChoiceItems(sortOptions, selectedOption) { _, which ->
                    selectedOption = which
                }
                .setPositiveButton("Show Results") { _, _ ->
                    sortListings(sortMap[sortOptions[selectedOption]]!!)
//                    setupRecyclerView()
                }
                .setNeutralButton("Cancel") { _, _ ->
                    setupRecyclerView()
                }
                .show()
        }
    }

    private fun setupRecyclerView() {
        val query = db.collection("RoommateListings")
            .whereNotEqualTo("userID", auth.currentUser!!.uid)
            .orderBy("moveInDate", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<RoommateListingData>()
            .setQuery(query, RoommateListingData::class.java)
            .build()

        adapter = RoommateListAdapter(options, requireContext(), this)

        binding.recyclerViewRoommates.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRoommates.adapter = adapter
        adapter.startListening()

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                if(adapter.itemCount<1) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.recyclerViewRoommates.isVisible = false
                    Glide.with(requireActivity()).load(R.drawable.empty_search).into(binding.emptyImg)
                } else {
                    binding.emptyLayout.visibility = View.GONE
                    binding.recyclerViewRoommates.isVisible = true
                }
            }
        })
    }

    private fun sortListings(sortField: String, order: String = "") {
        val direction = if (order.isNotEmpty() && order == "DESC") {
            Query.Direction.DESCENDING
        } else {
            Query.Direction.ASCENDING
        }

        val query = db.collection("RoommateListings")
            .whereNotEqualTo("userID", auth.currentUser!!.uid)
            .orderBy(sortField, direction)

        val newOptions = FirestoreRecyclerOptions.Builder<RoommateListingData>()
            .setQuery(query, RoommateListingData::class.java)
            .build()

        adapter.updateOptions(newOptions)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SearchListingUtil.setRoommateNavBarVisibility(requireActivity(), true)
        return binding.root
    }

    override fun handleListingData(data: RoommateListingData) {
        val action = RoommateSearchFragmentDirections.navigateSearchToDetailedListing(data)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
