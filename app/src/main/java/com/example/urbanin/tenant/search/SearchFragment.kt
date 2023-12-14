package com.example.urbanin.tenant.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.urbanin.BuildConfig
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.data.filterCount
import com.example.urbanin.databinding.FragmentSearchBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    // google map object (for callback)
//    private lateinit var googleMap: GoogleMap
    companion object {
        var isMapInit: Boolean = false
    }

    // FireStore object
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchBinding.inflate(layoutInflater)

        // initialize Places, Places API  autocomplete bar does not work without this
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        }

        FirebaseApp.initializeApp(requireContext())

        if(filterCount == 0) {
            binding.listingFilterCount.isVisible = false
        } else {
            binding.listingFilterCount.isVisible = true
            binding.listingFilterCount.text = filterCount.toString()
        }
        binding.listingSearchBarLayout.visibility = View.VISIBLE

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

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            childFragmentManager.findFragmentById(binding.listingSearchPlaces.id) as AutocompleteSupportFragment?
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
            override fun onError(p0: Status) {}

            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                // get details of the place selected
                val selectedPlaceName: String? = place.name
                val selectedPlaceLatLng: LatLng? = place.latLng
                // if location is valid, add marker and zoom to it
                if (selectedPlaceLatLng != null) {
                    if (selectedPlaceName != null) {
                        Log.d(TAG, "Place Selected: ${selectedPlaceName}\n Place Location: $selectedPlaceLatLng")
                    }
                }
            }
        })

        binding.listingModeView.setOnClickListener {
            val actionChangeView: NavDirections
            if (binding.listingModeView.text == "List") {
                binding.listingModeView.text = "Map"
                actionChangeView =
                    SearchMapViewFragmentDirections.actionSearchMapViewFragmentToSearchListViewFragment()
            } else {
                binding.listingModeView.text = "List"
                actionChangeView =
                    SearchListViewFragmentDirections.actionSearchListViewFragmentToSearchMapViewFragment()
            }
            binding.listingView.getFragment<NavHostFragment>().navController.navigate(
                actionChangeView
            )
        }

        binding.listingFilterText.setOnClickListener {
            // temporarily hide bottom nav bar, when inflating filter fragment (to show full screen)
            findNavController().navigate(SearchFragmentDirections.navigateTenantSearchToFilter())
        }
        // if filter fragment not active, bottom nav bar is visible
        setNavBarVisibility(true)
    }

    private fun setNavBarVisibility(flag: Boolean) {
        val parentNavBar: View = requireActivity().findViewById(R.id.bottom_navbar)
        parentNavBar.isVisible = flag
    }

}