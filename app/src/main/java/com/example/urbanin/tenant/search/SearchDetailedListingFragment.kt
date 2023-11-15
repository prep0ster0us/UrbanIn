package com.example.urbanin.tenant.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.urbanin.databinding.TenantSearchDetailedListingBinding
import com.example.urbanin.tenant.search.Amenities.AmenitiesAdapter
import com.example.urbanin.tenant.search.Amenities.AmenitiesCard
import com.google.firebase.firestore.FirebaseFirestore

class SearchDetailedListingFragment: Fragment() {

    private lateinit var binding: TenantSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore

    private val args: SearchDetailedListingFragmentArgs by navArgs<SearchDetailedListingFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TenantSearchDetailedListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()



        // check which amenities are available and only include those in the card view
        val amenitiesList: ArrayList<AmenitiesCard> = ArrayList()
        val aList = hashMapOf<String, Pair<Int, Boolean>>()
        for (amenity in aList) {
            if (amenity.value.second) {
                amenitiesList.add(
                    AmenitiesCard(
                        amenity.value.first,
                        amenity.key
                    )
                )
            }
        }

        val grid = binding.detailedListingAmenities
        grid.adapter = AmenitiesAdapter(
            requireContext(),
            amenitiesList
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
            // TODO: populate data for listing to respective view
//            binding.detailedImageGallery.resources = listingParcel.img
//            binding.detailedListingAmenities = listingParel.amenities
        }.root
    }
}