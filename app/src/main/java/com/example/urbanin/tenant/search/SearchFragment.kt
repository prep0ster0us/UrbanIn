package com.example.urbanin.tenant.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.databinding.FragmentSearchBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SearchFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentSearchBinding
    // google map object (for callback)
    private lateinit var googleMap: GoogleMap
    // for list view
    private lateinit var listingView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    // initialize map fragment and add async function callback (to avoid onCreate error if map takes time to load and onCreate() is requesting before it's available)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(binding.listingMapView.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // set map callback for google maps (i.e. set position and initial manifest properties
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.let{
            val newHaven = LatLng(41.293011385559716, -72.96167849997795)
            googleMap.addMarker(MarkerOptions().position(newHaven).title("Marker on University"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newHaven, 15F))
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null);
        }
    }

}