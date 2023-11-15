package com.example.urbanin.tenant.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.urbanin.R
import com.example.urbanin.databinding.TenantSearchDetailedListingBinding
import com.example.urbanin.tenant.search.Amenities.AmenitiesAdapter
import com.example.urbanin.tenant.search.Amenities.AmenitiesCard
import com.google.firebase.firestore.FirebaseFirestore

class SearchDetailedListingFragment: Fragment() {

    private lateinit var binding: TenantSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore

    private lateinit var amenitiesList: Map<String, Boolean>

    private val args: SearchDetailedListingFragmentArgs by navArgs<SearchDetailedListingFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TenantSearchDetailedListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        // check which amenities are available and only include those in the card view
        val amenitiesGrid: ArrayList<AmenitiesCard> = ArrayList()

        // drawable resource id map for icons for each amenity
        val amenitiesGridIcons = hashMapOf(
            "In-Unit Laundry" to R.drawable.amenities_laundry_24,
            "Security" to R.drawable.amenities_security_24,
            "gym" to R.drawable.amenities_gym_24,
            "pool" to R.drawable.amenities_pets_24
        )

        val amenitiesList = args.listing.amenities
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
            val listingParcel = args.listing
            binding.detailedListingTitle.text = listingParcel.title
            binding.detailedListingDescription.text = listingParcel.description
            amenitiesList = listingParcel.amenities
            // TODO: populate data for listing to respective view
//            binding.detailedImageGallery.resources = listingParcel.img
//            binding.detailedListingAmenities = listingParel.amenities
        }.root
    }
}