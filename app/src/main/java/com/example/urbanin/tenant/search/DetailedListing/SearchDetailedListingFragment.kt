package com.example.urbanin.tenant.search.DetailedListing

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.urbanin.R
import com.example.urbanin.data.ChatMessageData
import com.example.urbanin.data.FacilityAdapter
import com.example.urbanin.data.FacilityItem
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.MediaAdapter
import com.example.urbanin.data.MediaItem
import com.example.urbanin.data.MessageDataModel
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.TenantSearchDetailedListingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.text.NumberFormat

class SearchDetailedListingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: TenantSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var prefManager: LoginPreferenceManager

    private val args: SearchDetailedListingFragmentArgs by navArgs<SearchDetailedListingFragmentArgs>()

    private lateinit var amenitiesList: Map<String, Boolean>

    // for media gallery
    private lateinit var mediaAdapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TenantSearchDetailedListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        prefManager = LoginPreferenceManager(requireContext())

        binding.detailedListingTitle.text = args.listing.title
        binding.detailedListingSubTitle.text = args.listing.address
        binding.detailedListingRent.text = formatAsCurrency(args.listing.price.toFloat())

        setupMediaGallery()
        showAmenitiesGrid()
        showUtilitiesGrid()

        binding.detailedListingBackBtn.setOnClickListener {
            val action = SearchDetailedListingFragmentDirections.actionSearchDetailedListingFragmentToSearchListViewFragment()
            findNavController().navigate(action)
        }

        binding.detailedListingRoom.text = args.listing.numRooms
        binding.detailedListingBath.text = args.listing.numBaths
        binding.detailedListingType.text = args.listing.type
        binding.detailedListingFurnished.text = args.listing.furnished
        binding.detailedListingRent.text = formatAsCurrency(args.listing.price.toFloat())
        binding.detailedListingLocationAddress.text = args.listing.address

        // make button invisible if the listing was posted by user
        if(auth.currentUser != null) {
            if(auth.currentUser!!.uid == args.listing.userID) {
                binding.detailedListingMessageBtn.visibility = View.GONE
            }
        }
        // direct to messages tab if user chooses to message listing owner
        binding.detailedListingMessageBtn.setOnClickListener {
            if (auth.currentUser == null) {
                showLoginPrompt()
            } else {
                findNavController().navigate(
                    SearchDetailedListingFragmentDirections.navigateDetailedListingToChat(
                        ChatMessageData(
                            "",
                            args.listing.userID,
                            args.listing.address,
                            args.listing.img[0]
                        )
                    )
                )
            }
        }
        // init map
        val mapFragment =
            childFragmentManager.findFragmentById(binding.detailedListingLocationMap.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showLoginPrompt() {
        val builder = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.login_prompt_dialog_layout, null)
        val btnLogin = view.findViewById<Button>(R.id.promptLogin)
        btnLogin.setOnClickListener {
            prefManager.setRedirectContext("tenant_detailed_listing")
            findNavController().navigate(SearchDetailedListingFragmentDirections.navigateDetailedListingToLogin())
            builder.dismiss()
        }
        builder.setView(view)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.setCancelable(true)
        builder.show()
    }

    private fun setupMediaGallery() {
//        mediaAdapter = ListingMediaAdapter(args.listing.img, requireContext())
        val mediaList: MutableList<MediaItem> = mutableListOf()
        for (mediaFile in args.listing.img) {
            mediaList.add(
                MediaItem(
                    MediaItem.ItemType.IMAGE,
                    Uri.parse(mediaFile)
                )
            )
        }
        for (mediaFile in args.listing.vid) {
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

        // fetch all media for listing
    }

    private fun showUtilitiesGrid() {
        // check which amenities are available and only include those in the card view
        val utilGrid: ArrayList<FacilityItem> = ArrayList()

        // drawable resource id map for icons for each amenity
        val utilGridIcons = hashMapOf(
            "Electricity" to R.drawable.utilities_electricity_24,
            "Gas" to R.drawable.utilities_gas_24,
            "Water" to R.drawable.utilities_water_24,
            "Trash Removal" to R.drawable.utilities_trash_24,
            "Snow Removal" to R.drawable.utilities_snow_removal_24
        )

//        val utilList = args.listing.utilities
        for (util in args.listing.utilities) {
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
        // check which amenities are available and only include those in the card view
        val amenitiesGrid: ArrayList<FacilityItem> = ArrayList()

        // drawable resource id map for icons for each amenity
        val amenitiesGridIcons = hashMapOf(
            "Pets Allowed" to R.drawable.amenities_pets_24,
            "In-Unit laundry" to R.drawable.amenities_laundry_24,
            "HVAC System" to R.drawable.amenities_hvac_24,
            "24/7 Security" to R.drawable.amenities_security_24,
            "Gym" to R.drawable.amenities_gym_24,
            "Pool" to R.drawable.amenities_pool_24
        )

        val amenitiesList = args.listing.amenities
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

    private fun formatAsCurrency(value: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return "$ " + formatter.format(value).toString()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val listingLocation = LatLng(
            args.listing.latitude.toDouble(),
            args.listing.longitude.toDouble()
        )
        googleMap.let {
            // add marker at listing location
            addMarker(
                googleMap,
                listingLocation,
                args.listing.address
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.apply {
            val listingParcel = args.listing
            binding.detailedListingTitle.text = listingParcel.title
            binding.detailedListingDescription.text = listingParcel.description
            amenitiesList = listingParcel.amenities
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SearchListingUtil) {
            setTenantNavBarVisibility(requireActivity(), false)
            setSearchBarVisibility(requireActivity(), false)
        }
    }
}