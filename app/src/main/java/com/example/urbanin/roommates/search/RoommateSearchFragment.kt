package com.example.urbanin.roommates.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.data.RoommateListAdapter
import com.example.urbanin.data.RoommateListingData
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.FragmentRoommateSearchBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class RoommateSearchFragment : Fragment(), RoommateListAdapter.Callbacks {
    private lateinit var binding: FragmentRoommateSearchBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoommateListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentRoommateSearchBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
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


//    private fun setupOptionClickListeners(view: View) {
//        view.findViewById<TextView>(R.id.textViewViewOption).setOnClickListener {
//            // Handle View option click
//        }
//        view.findViewById<TextView>(R.id.textViewSortOption).setOnClickListener {
//            // Handle Sort option click
//        }
//        view.findViewById<TextView>(R.id.textViewFilterOption).setOnClickListener {
//            // Handle Filter option click
//        }
//    }
}
