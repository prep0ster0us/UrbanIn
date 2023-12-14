package com.example.urbanin.roommates.search

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.urbanin.R
import com.example.urbanin.data.ChatMessageData
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.MediaAdapter
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.RoommateSearchDetailedListingBinding
import com.example.urbanin.tenant.search.DetailedListing.SearchDetailedListingFragmentDirections
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.NumberFormat

class RoommateDetailedListingFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: RoommateSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val args: RoommateDetailedListingFragmentArgs by navArgs<RoommateDetailedListingFragmentArgs>()
    private lateinit var prefManager: LoginPreferenceManager

    // for media gallery
    private lateinit var mediaAdapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RoommateSearchDetailedListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        prefManager = LoginPreferenceManager(requireContext())

        if (args.rmListing.img.isNotEmpty()) {
            Picasso.get().load(args.rmListing.img).into(binding.detailedListingPfp)
        }
        binding.detailedListingTitle.text = args.rmListing.name
        binding.detailedListingSubTitle.text = if (args.rmListing.gender != "") {
            "${args.rmListing.occupation}, ${args.rmListing.gender} (${args.rmListing.age})"
        } else {
            "${args.rmListing.occupation} (${args.rmListing.age})"
        }
        binding.detailedListingDescription.text = args.rmListing.description
        binding.detailedListingBudget.text = formatAsCurrency(args.rmListing.budget.toFloat())
        binding.detailedListingOccupation.text = args.rmListing.occupation
        binding.detailedListingHobbies.text = args.rmListing.hobbies.toString().replace("[","").replace("]","")
        binding.detailedListingMoveIn.text = args.rmListing.moveInDate
        binding.detailedListingRoomSize.text = args.rmListing.roomSize

        binding.detailedListingLocationAddress.text = args.rmListing.address

        binding.detailedListingBackBtn.setOnClickListener {
//            val action = SearchDetailedListingFragmentDirections.actionSearchDetailedListingFragmentToSearchListViewFragment()
//            findNavController().navigate(action)
            findNavController().popBackStack()
        }

        // make button invisible if the rmListing was posted by user
        if ((auth.currentUser != null) and (auth.currentUser!!.uid == args.rmListing.userID)) {
            binding.detailedListingMessageBtn.visibility = View.GONE
        }
        // direct to messages tab if user chooses to message rmListing owner
        binding.detailedListingMessageBtn.setOnClickListener {
            findNavController().navigate(
                RoommateDetailedListingFragmentDirections.navigateDetailedRoommateToChat(
                    ChatMessageData(
                        "",
                        args.rmListing.userID,
                        args.rmListing.name,
                        args.rmListing.img
                    )
                )
            )
        }


        // init map
        val mapFragment =
            childFragmentManager.findFragmentById(binding.detailedListingLocationMap.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun formatAsCurrency(value: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return "$ " + formatter.format(value).toString()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val listingLocation = LatLng(
            args.rmListing.latitude.toDouble(),
            args.rmListing.longitude.toDouble()
        )
        googleMap.let {
            // move map to user location
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SearchListingUtil) {
            setRoommateNavBarVisibility(requireActivity(), false)
        }
    }
}