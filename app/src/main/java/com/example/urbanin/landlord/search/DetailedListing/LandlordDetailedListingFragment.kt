package com.example.urbanin.landlord.search.DetailedListing

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.data.FacilityAdapter
import com.example.urbanin.data.FacilityItem
import com.example.urbanin.data.MediaAdapter
import com.example.urbanin.data.MediaItem
import com.example.urbanin.databinding.LandlordSearchDetailedListingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.text.NumberFormat

class LandlordDetailedListingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: LandlordSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore

    private lateinit var amenitiesList: Map<String, Boolean>

    private val args: LandlordDetailedListingFragmentArgs by navArgs<LandlordDetailedListingFragmentArgs>()

    // for media gallery
    private lateinit var mediaAdapter: MediaAdapter

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
        Toast.makeText(requireContext(), args.landlordListing.price, Toast.LENGTH_SHORT).show()
        binding.detailedListingRent.text = formatAsCurrency(args.landlordListing.price.toFloat())
        binding.detailedListingLocationAddress.text = args.landlordListing.address

        // hide main bottom nav bar
        setNavBarVisibility(false)

        binding.detailedListingEditBtn.setOnClickListener {
            val action = LandlordDetailedListingFragmentDirections.navigateFromDetailedListingToEditListing(args.landlordListing)
            findNavController().navigate(action)
        }
    }

    private fun setupMediaGallery() {
//        mediaAdapter = ListingMediaAdapter(args.listing.img, requireContext())
        val mediaList: MutableList<MediaItem> = mutableListOf()
        for (mediaFile in args.landlordListing.img) {
            mediaList.add(
                MediaItem(
                    MediaItem.ItemType.IMAGE,
                    Uri.parse(mediaFile)
                )
            )
        }
        for (mediaFile in args.landlordListing.vid) {
            mediaList.add(
                MediaItem(
                    MediaItem.ItemType.VIDEO,
                    Uri.parse(mediaFile)
                )
            )
        }
        mediaAdapter = MediaAdapter(mediaList, requireContext(), false)

        binding.detailedImageGallery.adapter = mediaAdapter
        binding.imageGalleryDots.attachTo(binding.detailedImageGallery)

        val mapFragment =
            childFragmentManager.findFragmentById(binding.detailedListingLocationMap.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // fetch all media for listing
    }

    private fun showUtilitiesGrid() {
        // drawable resource id map for icons for each amenity
        val utilGridIcons = hashMapOf(
            "Electricity" to R.drawable.utilities_electricity_24,
            "Gas" to R.drawable.utilities_gas_24,
            "Water" to R.drawable.utilities_water_24,
            "Trash Removal" to R.drawable.utilities_trash_24,
            "Snow Removal" to R.drawable.utilities_snow_removal_24
        )

        // check which amenities are available and only include those in the card view
        val utilGrid: ArrayList<FacilityItem> = ArrayList()
        for (util in args.landlordListing.utilities) {
            if (util.value) {
                utilGrid.add(
                    FacilityItem(
                        utilGridIcons[util.key]!!,
                        util.key
                    )
                )
            }
        }

        binding.detailedListingUtilities.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = FacilityAdapter(requireContext(), utilGrid)
        }
    }

    private fun showAmenitiesGrid() {
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
        // check which amenities are available and only include those in the card view
        val amenitiesGrid: ArrayList<FacilityItem> = ArrayList()
        for (amenity in amenitiesList) {
            if (amenity.value) {
                amenitiesGrid.add(
                    FacilityItem(
                        amenitiesGridIcons[amenity.key]!!,
                        amenity.key
                    )
                )
            }
        }

        binding.detailedListingAmenities.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = FacilityAdapter(requireContext(), amenitiesGrid)
        }
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