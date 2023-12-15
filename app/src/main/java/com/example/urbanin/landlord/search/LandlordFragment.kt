package com.example.urbanin.landlord.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.data.FilterListingUtil
import com.example.urbanin.data.ListingAdapter
import com.example.urbanin.data.ListingData
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.SortParameters
import com.example.urbanin.data.filterCount
import com.example.urbanin.data.filterParameters
import com.example.urbanin.data.ifFiltered
import com.example.urbanin.data.sortOptions
import com.example.urbanin.data.userListingCollection
import com.example.urbanin.databinding.FragmentLandlordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class LandlordFragment : Fragment(), ListingAdapter.Callbacks {
    // TODO: <known flaw> first app login into this view still requests for biometric enable (dialog); had this in last sprint; haven't removed

    //    private var _binding: FragmentLandlordBinding? = null
//    private val binding get() = _binding!!
    private lateinit var binding: FragmentLandlordBinding

    private var db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // for shared preferences
    private lateinit var prefManager: LoginPreferenceManager

    private lateinit var adapter: ListingAdapter

    //    private var userListings: ArrayList<ListingData> = arrayListOf()
    private var userListings: ArrayList<String> = arrayListOf()

    // for sorting
    private var sortParams: SortParameters = SortParameters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentLandlordBinding.inflate(layoutInflater)

        prefManager = LoginPreferenceManager(requireContext())

        // if first login, show dialog to ask user to opt in for biometric login
        if (prefManager.isFirstLogin()) {
            prefManager.setFirstLogin()
            showOptForBiometricDialog()
        }

        // set filter count
        if (filterCount == 0) {
            binding.landlordSearchFilterCount.isVisible = false
        } else {
            binding.landlordSearchFilterCount.isVisible = true
            binding.landlordSearchFilterCount.text = filterCount.toString()
        }

        binding.landlordListingFAB.setOnClickListener {
            val action = LandlordFragmentDirections.navigateToLandlordAddListing()
            findNavController().navigate(action)
        }
        userListingCollection = arrayListOf()

    }

    private fun showOptForBiometricDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogPalette)
            .setTitle("Enable biometric login")
            .setIcon(R.drawable.baseline_fingerprint_24)
            .setMessage("Would you like to enable biometric login for easier access?\nYou can also enable it from the settings later on.")
            .setPositiveButton(
                "I'M IN!"
            ) { _, _ ->
                prefManager.setBiometricEnabled(true)
            }
            .setNegativeButton(
                "OPT-OUT"
            ) { _, _ ->
                prefManager.setBiometricEnabled(false)
            }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        _binding = FragmentLandlordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        sortListings()
        // Load your listings data into the 'listings' list
        loadData()

        binding.landlordSearchSort.setOnClickListener {
            // sort listings
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogPalette)
                .setTitle("Sort By")
                .setSingleChoiceItems(
                    sortOptions,
                    sortParams.selectedOption
                ) { _, which ->
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
//            sortListings()
//            setupRecyclerView()
        }

        binding.landlordSearchFilter.setOnClickListener {
            // temporarily hide bottom nav bar, when inflating filter fragment (to show full screen)
            findNavController().navigate(LandlordFragmentDirections.navigateToLandlordFilterFragment())
        }

        // show bottom nav bar
        setNavBarVisibility(true)
    }

    private fun setNavBarVisibility(flag: Boolean) {
        val parentNavBar: View = requireActivity().findViewById(R.id.bottomNavigationView)
        parentNavBar.isVisible = flag
    }

    private fun setupRecyclerView() {
        binding.landlordListingRecyclerView.setHasFixedSize(true)
        binding.landlordListingRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ListingAdapter(userListingCollection, context, "landlord", this)
        binding.landlordListingRecyclerView.adapter = adapter
    }

    private fun sortListings() {
        val roomComparator = mutableListOf("Studio", "1", "2", "3", "4", "5")
        val bathComparator = mutableListOf("1", "1.5", "2", "3", "4")

        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        when (sortParams.sortBy) {
            "Latest" -> userListingCollection.sortBy {
                LocalDate.parse(
                    it.datePosted,
                    dateTimeFormatter
                )
            }

            "Rent: Low to High" -> userListingCollection.sortBy { it.price.toLong() }
            "Rent: High to Low" -> userListingCollection.sortByDescending { it.price.toLong() }
            "Number of Rooms" -> userListingCollection.sortBy { roomComparator.indexOf(it.numRooms) }
            "Number of Baths" -> userListingCollection.sortBy { bathComparator.indexOf(it.numBaths) }
            else -> userListingCollection
        }
    }

    private fun loadData() {
        // Load in data from Firestore
        getListingsFromDatabase()
    }

    private fun getListingsFromDatabase() {
        db.collection("Users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "data fetched for user: ${auth.currentUser!!.uid}__")

                userListings = document.data!!["Listings"] as ArrayList<String>
                if(userListings.size < 1) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.landlordListingRecyclerView.visibility = View.INVISIBLE
                    Glide.with(requireActivity()).load(R.drawable.empty_search).into(binding.emptyImg)
                } else {
                    binding.emptyLayout.visibility = View.GONE
                    binding.landlordListingRecyclerView.visibility = View.VISIBLE
                }
                for (listingId in userListings) {
                    // check if listing already in the recycler view collection
                    var checkExisting = false
                    for (existingListing in userListingCollection) {
                        if (existingListing.listingID == listingId) {
                            checkExisting = true
                            break
                        }
                    }

                    if (!checkExisting) {
                        db.collection("Listings")
                            .document(listingId)
                            .get()
                            .addOnSuccessListener { doc ->
                                if (doc != null) {
                                    // check if this listing already exists in the recycler list (for listings)
                                    userListingCollection.add(
                                        ListingData(
                                            listingId,
                                            doc.data!!["userID"] as String,
                                            doc.data!!["type"] as String,
                                            doc.data!!["title"] as String,
                                            doc.data!!["description"] as String,
                                            doc.data!!["latitude"] as String,
                                            doc.data!!["longitude"] as String,
                                            doc.data!!["address"] as String,
                                            doc.data!!["price"] as String,
                                            doc.data!!["img"] as MutableList<String>,
                                            doc.data!!["vid"] as MutableList<String>,
                                            doc.data!!["datePosted"] as String,
                                            doc.data!!["availableFrom"] as String,
                                            doc.data!!["numRooms"] as String,
                                            doc.data!!["numBaths"] as String,
                                            doc.data!!["furnished"] as String,
                                            doc.data!!["utilities"] as Map<String, Boolean>,
                                            doc.data!!["amenities"] as Map<String, Boolean>
                                        )
                                    )
                                    // refresh recycler view, and load in listings (as and when fetched from firestore)
                                    adapter.notifyDataSetChanged()
                                } else {
                                    Log.d(TAG, "Listing fetch: NO-DATA - No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Listing fetch: ERROR - " + exception.message.toString())
                            }
                    }
                }
                // after getting all listings, filter out based on parameters (if any filters set)
                if (ifFiltered) {
                    filterUserListings()
                    adapter.notifyDataSetChanged()
                }
                binding.landlordListingProgressLayout.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.d(TAG, "data fetch FAILED: $it")
            }
    }

    private fun filterUserListings() {
        val iterator = userListingCollection.iterator()
        while (iterator.hasNext()) {
            val listing = iterator.next()
            if (FilterListingUtil.checkIfMatches(listing, filterParameters)) {
                // if listing does not match ANY of the filter parameters, remove listing from collection (to be displayed in search view)
                iterator.remove()
            }
        }
    }

    override fun handleListingData(data: ListingData, flag: String) {
        when (flag) {
            "landlord" -> {
                val action =
                    LandlordFragmentDirections.navigateToLandlordDetailedListingFragment(data)
                findNavController().navigate(action)
            }

            "delete" -> {
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogPalette)
                    .setTitle("Confirm")
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setMessage("Do you wish to remove this listing?\nNOTE: This action is irreversible!")
                    .setPositiveButton("Yes") { _, _ ->
                        // remove from database
                        deleteAndUpdate(data)
                        userListingCollection.remove(data)
                        // refresh recycler view
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Cancel", null)
                    .setCancelable(true)
                    .show()

                val action =
                    LandlordFragmentDirections.navigateToLandlordDetailedListingFragment(data)
                findNavController().navigate(action)
            }
        }
    }

    private fun deleteAndUpdate(data: ListingData) {
        // remove from listings database
        db.collection("Listings")
            .document(data.listingID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Deletion SUCCESS: ${data.listingID}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Deletion FAILURE: $exception")
            }
        // remove from user's database
        db.collection("Users")
            .document(data.userID)
            .update("Listings", FieldValue.arrayRemove(data.listingID))
            .addOnSuccessListener {
                Log.d(TAG, "Deletion SUCCESS: ${auth.currentUser!!.uid} removed ${data.listingID}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Deletion from user FAILURE: $exception")
            }
    }
}
