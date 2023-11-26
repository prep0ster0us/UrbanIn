package com.example.urbanin.landlord

import LandlordListingAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.auth.LoginPreferenceManager
import com.example.urbanin.databinding.FragmentLandlordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LandlordFragment : Fragment(), LandlordListingAdapter.Callbacks {

    //    private var _binding: FragmentLandlordBinding? = null
//    private val binding get() = _binding!!
    private lateinit var binding: FragmentLandlordBinding

    private var db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // for shared preferences
    private lateinit var prefManager: LoginPreferenceManager

    private lateinit var adapter: LandlordListingAdapter

    //    private var userListings: ArrayList<ListingData> = arrayListOf()
    private var userListings: ArrayList<String> = arrayListOf()
    private var recyclerList = ArrayList<LandlordListingData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentLandlordBinding.inflate(layoutInflater)

        prefManager = LoginPreferenceManager(requireContext())

        // if first login, show dialog to ask user to opt in for biometric login
        Log.d(TAG, prefManager.isFirstLogin().toString())
        if (prefManager.isFirstLogin()) {
            prefManager.setFirstLogin()
            showOptForBiometricDialog()
        }

        binding.addListingFAB.setOnClickListener {
            val action = LandlordFragmentDirections.navigateToLandlordAddListing()
            findNavController().navigate(action)
        }

        // TODO: test
        /*
        db.collection("Users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {user ->
//                db.collection("Listings")
//                    .document(it.id.toString())
//                    .get()
//                    .addOnSuccessListener { document ->
//                        Toast.makeText(requireContext(), "id: ${document.id}  data: ${document.data}", Toast.LENGTH_SHORT).show()
//                        Toast.makeText(requireContext(), "sample data: ${document.data!!["userID"]}", Toast.LENGTH_SHORT).show()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
//                    }

//                Toast.makeText(requireContext(), "user id: ${user.id}; data: ${user.data}", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"user id: ${user.id}; data: ${user.data}")
                db.collection("Listings")
                    .document("IvkTM1LYTbGnTFaLyERM_")
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val document = task.result
                            Toast.makeText(
                                requireContext(),
                                "id: ${document.id}  data: ${document.data}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Toast.makeText(
                                requireContext(),
                                "sample data: ${document.data!!["userID"]}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "couldn't find user", Toast.LENGTH_SHORT).show()
            }
*/
    }

    private fun showOptForBiometricDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Enable biometric login")
            .setMessage("Would you like to enable biometric login for easier access?")
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
        // TODO: Load your listings data into the 'listings' list
        loadData()

        // show bottom nav bar
        val parentNavBar: View = requireActivity().findViewById(R.id.bottomNavigationView)
        parentNavBar.isVisible = true
    }

    private fun setupRecyclerView() {
//        val recyclerList: ArrayList<LandlordListingData> = arrayListOf()
//        for (listing in userListingCollection) {
//            recyclerList.add(listing)
//        }
        binding.landlordListingRecyclerView.setHasFixedSize(true)
        binding.landlordListingRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = LandlordListingAdapter(userListingCollection, context,this)
        binding.landlordListingRecyclerView.adapter = adapter
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
//        adapter.notifyDataSetChanged()
    }

    private fun getListingsFromDatabase() {
//        TODO("Not yet implemented")
        db.collection("Users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "data fetched for user: ${auth.currentUser!!.uid}__")
//                userListings = document.data!!["Listings"] as ArrayList<ListingData>

                userListings = document.data!!["Listings"] as ArrayList<String>
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
                                Log.d(TAG, "Listing fetch: SUCCESS - id= " + doc.id)
                                Log.d(TAG, "doc data: $doc")
                                if (doc != null) {
                                    // check if this listing already exists in the recycler list (for listings)
                                    Toast.makeText(requireContext(), doc.data!!["userID"].toString(), Toast.LENGTH_SHORT).show()
                                    userListingCollection.add(
                                        LandlordListingData(
                                            listingId,
//                                            auth.currentUser!!.uid,
                                            doc.data!!["userID"] as String,
                                            doc.data!!["type"] as String,
                                            doc.data!!["title"] as String,
                                            doc.data!!["description"] as String,
                                            doc.data!!["latitude"] as String,
                                            doc.data!!["longitude"] as String,
                                            doc.data!!["address"] as String,
                                            doc.data!!["price"] as String,
                                            doc.data!!["img"] as MutableList<String>,
                                            doc.data!!["datePosted"] as String,
                                            doc.data!!["availableFrom"] as String,
                                            doc.data!!["numRooms"] as String,
                                            doc.data!!["numBaths"] as String,
                                            doc.data!!["furnished"] as String,
                                            doc.data!!["utilities"] as Map<String, Boolean>,
                                            doc.data!!["amenities"] as Map<String, Boolean>
                                        )
                                    )
                                } else {
                                    Log.d(TAG, "Listing fetch: NO-DATA - No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Listing fetch: ERROR - " + exception.message.toString())
                            }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d(TAG, "data fetch FAILED: $it")
            }
    }

//    private fun getUserListings(userListings: ArrayList<String>) {
//
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    override fun handleListingData(data: LandlordListingData) {
        val action = LandlordFragmentDirections.navigateToLandlordDetailedListingFragment(data)
        findNavController().navigate(action)
    }
}
