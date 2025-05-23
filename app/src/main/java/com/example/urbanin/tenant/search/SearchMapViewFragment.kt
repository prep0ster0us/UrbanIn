package com.example.urbanin.tenant.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.data.FilterListingUtil
import com.example.urbanin.data.ListingData
import com.example.urbanin.data.SortParameters
import com.example.urbanin.data.filterParameters
import com.example.urbanin.data.ifFiltered
import com.example.urbanin.data.listingCollection
import com.example.urbanin.databinding.FragmentSearchMapViewBinding
import com.example.urbanin.tenant.search.SearchFragment.Companion.isMapInit
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class SearchMapViewFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentSearchMapViewBinding

    // Firestore database instance
    private lateinit var db: FirebaseFirestore

    // google map object (for callback)
    private lateinit var googleMap: GoogleMap
    private lateinit var currentLocation: LatLng

    // for sorting
    private var sortParams = SortParameters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchMapViewBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()

        listingCollection = arrayListOf()
        getListingFromFirebase()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getListingFromFirebase() {
        db.collection("Listings")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    Log.d(TAG, "Document ${doc.id} => ${doc.data}")

                    // check if listing already exists in our local cached collection
                    var checkExisting = false
                    for (listing in listingCollection) {
                        if (listing.listingID == doc.id) {
                            checkExisting = true
                            break
                        }
                    }
                    // if doesn't exist, extract details and add to cached collection
                    if (!checkExisting) {
                        listingCollection.add(
                            ListingData(
                                doc.id,
                                doc.data["userID"] as String,
                                doc.data["type"] as String,
                                doc.data["title"] as String,
                                doc.data["description"] as String,
                                doc.data["latitude"] as String,
                                doc.data["longitude"] as String,
                                doc.data["address"] as String,
                                doc.data["price"] as String,
                                doc.data["img"] as MutableList<String>,
                                doc.data["vid"] as MutableList<String>,
                                doc.data["datePosted"] as String,
                                doc.data["availableFrom"] as String,
                                doc.data["numRooms"] as String,
                                doc.data["numBaths"] as String,
                                doc.data["furnished"] as String,
                                doc.data["utilities"] as Map<String, Boolean>,
                                doc.data["amenities"] as Map<String, Boolean>
                            )
                        )
                    }
                }
                // after getting all listings, filter out based on parameters (if any filters set)
                if (ifFiltered) {
                    filterListings()
                }
                // sort listings, based on sort value
                sortListings()
                // once all documents fetched, instantiate map view
                // and add listing markers
                val mapFragment =
                    childFragmentManager.findFragmentById(binding.searchListingMapView.id) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
            .addOnFailureListener {
                Log.w(TAG, "Error getting data!")
            }
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

    private fun filterListings() {
        val iterator = listingCollection.iterator()
        while (iterator.hasNext()) {
            val listing = iterator.next()
            if (FilterListingUtil.checkIfMatches(listing, filterParameters)) {
                // if listing does not match any of the filter parameters, remove listing from collection (to be displayed in search view)
                iterator.remove()
            }
        }
    }

    // set map callback for google maps (i.e. set position and initial manifest properties
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady called")

        // save local instance of googleMap
        this.googleMap = googleMap
        googleMap.let {
            // add markers for each listing on the map
            for (listing in listingCollection) {
                addMarker(
                    googleMap,
                    LatLng(listing.latitude.toDouble(), listing.longitude.toDouble()),
                    listing.address
                )
            }
            // check if can fetch current location
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        // check for location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
            // using ActivityContractResults (**cannot handle response action)
            /*
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
             */
        }
        zoomToCurrentLocation()
    }

    // using ActivityContractResults (**cannot handle response action)
    /*
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            canAccessLocation = if (!isGranted.values.contains(false)) {
                Log.d(TAG, "Both permissions : GRANTED")
                Toast.makeText(requireContext(), "just allowed", Toast.LENGTH_SHORT).show()
                true
            } else {
                Log.w(TAG, "Location Access Permission: NOT GRANTED")
                false
            }
        }
     */

    // *******************************************************************************************************************************
    // NOTE: The reason we're using the deprecated approach for requesting permissions (in place of the new ActivityContractResults)
    // is because we want to trigger the zoomToCurrentLocation(); when the user is prompted to allow permission and selects "Allow";
    // this trigger-based action is not possible with ActivityContractResults; since it is async in nature.
    // *******************************************************************************************************************************
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == 1001) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() &&
                        grantResults.contains(PackageManager.PERMISSION_GRANTED))
            ) {
                Log.d(TAG, "Both permissions : GRANTED")
                // zoom to current location, based on when user selects "allow" to permission alert
                zoomToCurrentLocation()
            } else {
                Log.w(TAG, "Location Access Permission: NOT GRANTED")
                // if not, zoom to last available listing (in the database)
                if (listingCollection.isNotEmpty()) {
                    val lastMarker = listingCollection.last()
                    // move camera to location
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(lastMarker.latitude.toDouble(), lastMarker.longitude.toDouble()),
                            10F
                        )
                    )
                    // Zoom out to zoom level 10, animating with a duration of 3 seconds.
                    if (!isMapInit) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10F), 3000, null)
                        isMapInit = true
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")      // Suppressing missing permission alert, since we are handling permissions separately.
    private fun zoomToCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(TAG, "Current location fetch: SUCCESS")
                    val markerPosition = LatLng(
                        location.latitude,
                        location.longitude
                    )
                    // zoom to current location
                    if (!isMapInit) {
                        zoomToLocation(googleMap, markerPosition)
                        isMapInit = true
                    } else {
                        // if already initialized, move camera (without zoom)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 10F))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to retrieve current location: ${exception.message}")
            }
    }

    private fun addMarker(googleMap: GoogleMap, location: LatLng?, markerTitle: String) {
        val customMarkerIcon = BitmapFactory.decodeResource(resources, R.drawable.map_marker_icon)
        val customMarker =
            BitmapDescriptorFactory.fromBitmap(customMarkerIcon.scale(120, 120, false))
        googleMap.addMarker(
            MarkerOptions().position(location!!).title(markerTitle).icon(customMarker)
        )
    }

    private fun zoomToLocation(googleMap: GoogleMap, location: LatLng) {
        Log.d(TAG, "zoomToLocation called")
        // move camera to location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10F))
        // Zoom out to zoom level 10, animating with a duration of 3 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10F), 3000, null)
    }

}