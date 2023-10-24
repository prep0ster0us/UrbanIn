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
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // google map object (for callback)
    private lateinit var googleMap: GoogleMap

    private lateinit var listingView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentSearchBinding.inflate(layoutInflater).root
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
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(newHaven))
        }
    }

}