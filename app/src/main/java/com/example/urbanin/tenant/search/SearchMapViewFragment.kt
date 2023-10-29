package com.example.urbanin.tenant.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.urbanin.BuildConfig
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentSearchMapViewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places

class SearchMapViewFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentSearchMapViewBinding
    // google map object (for callback)
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchMapViewBinding.inflate(layoutInflater)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(binding.searchListingMapView.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
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