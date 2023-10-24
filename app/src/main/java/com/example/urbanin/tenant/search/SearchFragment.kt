package com.example.urbanin.tenant.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.databinding.FragmentSearchBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class SearchFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentSearchBinding? = null

    private lateinit var googleMap: GoogleMap

    private val binding get() = _binding!!
    private lateinit var listingView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.listingMapView.onCreate(savedInstanceState)
        binding.listingMapView.onResume()

        binding.listingMapView.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentSearchBinding.inflate(layoutInflater).root
    }

    override fun onMapReady(map: GoogleMap) {
        map.let{
            googleMap = it
        }
    }

}