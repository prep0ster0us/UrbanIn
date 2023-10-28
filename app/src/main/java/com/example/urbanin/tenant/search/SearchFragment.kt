package com.example.urbanin.tenant.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.BuildConfig.MAPS_API_KEY
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.databinding.FragmentSearchBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class SearchFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentSearchBinding
    // google map object (for callback)
    private lateinit var googleMap: GoogleMap
    // for list view
    private lateinit var listingView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchBinding.inflate(layoutInflater)

        // initialize Places, Places API  autocomplete bar does not work without this
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), MAPS_API_KEY);
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    // initialize map fragment and add async function callback (to avoid onCreate error if map takes time to load and onCreate() is requesting before it's available)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(binding.listingMapView.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(binding.listingSearchPlaces.id) as AutocompleteSupportFragment?
        // Specify the types of place data to return.
        autocompleteFragment!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                // get details of the place selected
                val selectedPlaceName: String? = place.name
                val selectedPlaceLatLng: LatLng? = place.latLng
                // if location is valid, add marker and zoom to it
                if (selectedPlaceLatLng != null) {
                    if (selectedPlaceName != null) {
                        googleMap.clear()
                        zoomToLocation(googleMap, selectedPlaceLatLng, selectedPlaceName)
                    }
                }
//                val latitude = latlng?.latitude
//                val longitude = latlng?.longitude
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
                Toast.makeText(requireContext(), "some error occurred for search: ", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // set map callback for google maps (i.e. set position and initial manifest properties
    override fun onMapReady(googleMap: GoogleMap) {
        // save local instance of googleMap
        this.googleMap = googleMap
        googleMap.let{
            val newHaven = LatLng(41.293011385559716, -72.96167849997795)
            googleMap.addMarker(MarkerOptions().position(newHaven).title("Marker on University"))
            zoomToLocation(googleMap, newHaven, "Marker on University")
        }
    }
    private fun zoomToLocation(googleMap: GoogleMap, location: LatLng, markerTitle: String) {
        // add marker at location, and add marker title
        googleMap.addMarker(MarkerOptions().position(location).title(markerTitle))
        // move camera to location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15F))
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null)
    }

}