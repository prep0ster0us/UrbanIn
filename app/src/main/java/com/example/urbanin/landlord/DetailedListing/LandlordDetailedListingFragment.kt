package com.example.urbanin.landlord.DetailedListing

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.scale
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.LandlordSearchDetailedListingBinding
import com.example.urbanin.tenant.search.Amenities.AmenitiesAdapter
import com.example.urbanin.tenant.search.Amenities.AmenitiesCard
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.ceil

class LandlordDetailedListingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: LandlordSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore

    private lateinit var amenitiesList: Map<String, Boolean>

    private val args: LandlordDetailedListingFragmentArgs by navArgs<LandlordDetailedListingFragmentArgs>()

    // for media gallery
    private lateinit var mediaAdapter: LandlordListingMediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LandlordSearchDetailedListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        binding.detailedListingTitle.text = args.landlordListing.title
        binding.detailedListingSubTitle.text = args.landlordListing.address
        binding.detailedListingDescriptionHeader.text = "About ${args.landlordListing.title}"
        binding.detailedListingBackBtn.setOnClickListener{
            findNavController().navigate(LandlordDetailedListingFragmentDirections.navigateFromDetailedListingToSearch())
        }
        binding.detailedListingRoom.text = args.landlordListing.numRooms
        binding.detailedListingBath.text = args.landlordListing.numBaths
        binding.detailedListingType.text = args.landlordListing.type
        binding.detailedListingFurnished.text = args.landlordListing.furnished

        setupMediaGallery()

        showAmenitiesGrid()
        showUtilitiesGrid()
        binding.detailedListingRent.text = formatAsCurrency(args.landlordListing.price.toFloat())
        binding.detailedListingLocationAddress.text = args.landlordListing.address

        // hide main bottom nav bar
        setNavBarVisibility(false)

//        binding.detailedListingMessageBtn.setOnClickListener {  }
    }

    private fun setupMediaGallery() {
//        mediaAdapter = ListingMediaAdapter(args.listing.img, requireContext())
        val mediaList: MutableList<LandlordListingMediaItem> = mutableListOf()
        for (mediaFile in args.landlordListing.img) {
            mediaList.add(
                LandlordListingMediaItem(
                    LandlordListingMediaItem.ItemType.IMAGE,
                    Uri.parse(mediaFile)
                )
            )
        }
        for (mediaFile in args.landlordListing.vid) {
            mediaList.add(
                LandlordListingMediaItem(
                    LandlordListingMediaItem.ItemType.VIDEO,
                    Uri.parse(mediaFile)
                )
            )
        }
        mediaAdapter = LandlordListingMediaAdapter(mediaList, requireContext())

        binding.detailedImageGallery.adapter = mediaAdapter
        binding.imageGalleryDots.attachTo(binding.detailedImageGallery)

        val mapFragment =
            childFragmentManager.findFragmentById(binding.detailedListingLocationMap.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // fetch all media for listing
    }

    private fun showUtilitiesGrid() {
        // check which amenities are available and only include those in the card view
        val utilGrid: ArrayList<AmenitiesCard> = ArrayList()

        // drawable resource id map for icons for each amenity
        val utilGridIcons = hashMapOf(
            "Electricity" to R.drawable.utilities_electricity_24,
            "Gas" to R.drawable.utilities_gas_24,
            "Water" to R.drawable.utilities_water_24,
            "Trash Removal" to R.drawable.utilities_trash_24,
            "Snow Removal" to R.drawable.utilities_snow_removal_24
        )

//        val utilList = args.listing.utilities
        for (util in args.landlordListing.utilities) {
            if (util.value) {
                utilGrid.add(
                    AmenitiesCard(
                        utilGridIcons[util.key]!!,
                        util.key
                    )
                )
            }
        }

        val grid = binding.detailedListingUtilities
        grid.adapter = AmenitiesAdapter(
            requireContext(),
            utilGrid
        )
    }

    private fun showAmenitiesGrid() {
        // check which amenities are available and only include those in the card view
        val amenitiesGrid: ArrayList<AmenitiesCard> = ArrayList()

        // drawable resource id map for icons for each amenity
        val amenitiesGridIcons = hashMapOf(
            "Pets Allowed" to R.drawable.amenities_pets_24,
            "In-Unit laundry" to R.drawable.amenities_laundry_24,
            "HVAC System" to R.drawable.amenities_hvac_24,
            "24/7 Security" to R.drawable.amenities_security_24,
            "Gym" to R.drawable.amenities_gym_24,
            "Pool" to R.drawable.amenities_gym_24
        )

        val amenitiesList = args.landlordListing.amenities
        for (key in amenitiesList.keys) {
            Log.i(TAG, key + " ; in keyList=" + amenitiesList.containsKey(key))
        }
        for (amenity in amenitiesList) {
            if (amenity.value) {
                amenitiesGrid.add(
                    AmenitiesCard(
                        amenitiesGridIcons[amenity.key]!!,
                        amenity.key
                    )
                )
            }
        }

        val grid = binding.detailedListingAmenities
        grid.adapter = AmenitiesAdapter(
            requireContext(),
            amenitiesGrid
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return binding.root
        return binding.apply {
            val listingParcel = args.landlordListing
            detailedListingTitle.text = listingParcel.title
            detailedListingDescription.text = listingParcel.description
            amenitiesList = listingParcel.amenities
            // TODO: populate data for listing to respective view
//            binding.detailedImageGallery.resources = listingParcel.img
//            binding.detailedListingAmenities = listingParel.amenities
        }.root
    }

    private fun setNavBarVisibility(flag: Boolean) {
        val parentNavBar: View = requireActivity().findViewById(R.id.bottomNavigationView)
        parentNavBar.isVisible = flag
    }

    private fun formatAsCurrency(value: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return "$ " + formatter.format(value).toString()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val listingLocation = LatLng(
            args.landlordListing.latitude.toDouble(),
            args.landlordListing.longitude.toDouble()
        )
        googleMap.let {
//            googleMap.uiSettings.isZoomGesturesEnabled = true
//            googleMap.uiSettings.isScrollGesturesEnabled = true
            // add marker at listing location
            addMarker(
                googleMap,
                listingLocation,
                args.landlordListing.address
            )
            // move map to listing location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listingLocation, 16F))

            // allow child scrolling
            googleMap.setOnCameraMoveStartedListener {
                binding.detailedListingLocationMap.parent.requestDisallowInterceptTouchEvent(true)
            }
            googleMap.setOnCameraIdleListener {
                binding.detailedListingLocationMap.parent.requestDisallowInterceptTouchEvent(false)
            }
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
}