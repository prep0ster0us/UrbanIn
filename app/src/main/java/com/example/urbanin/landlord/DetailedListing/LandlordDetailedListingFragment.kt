package com.example.urbanin.landlord.DetailedListing

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.LandlordSearchDetailedListingBinding
import com.example.urbanin.databinding.TenantSearchDetailedListingBinding
import com.example.urbanin.tenant.search.Amenities.AmenitiesAdapter
import com.example.urbanin.tenant.search.Amenities.AmenitiesCard
import com.example.urbanin.tenant.search.DetailedListing.ListingMediaItem
import com.example.urbanin.tenant.search.DetailedListing.SearchDetailedListingFragmentArgs
import com.google.firebase.firestore.FirebaseFirestore

class LandlordDetailedListingFragment : Fragment() {

    private lateinit var binding: LandlordSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore

    private lateinit var amenitiesList: Map<String, Boolean>

    private val args: LandlordDetailedListingFragmentArgs by navArgs<LandlordDetailedListingFragmentArgs>()

    // for media gallery
    private lateinit var mediaAdapter: LandlordListingMediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LandlordSearchDetailedListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        binding.detailedListingTitle.text = args.landlordListing.title
        binding.detailedListingSubTitle.text = args.landlordListing.address

        setupMediaGallery()

        showAmenitiesGrid()
        showUtilitiesGrid()
    }

    private fun setupMediaGallery() {
//        mediaAdapter = ListingMediaAdapter(args.listing.img, requireContext())
        val mediaList: MutableList<LandlordListingMediaItem> = mutableListOf()
        for (mediaFile in args.landlordListing.img) {
            Toast.makeText(requireContext(), args.landlordListing.img.toString(), Toast.LENGTH_SHORT).show()
            mediaList.add(
                LandlordListingMediaItem(
                    LandlordListingMediaItem.ItemType.IMAGE,
                    Uri.parse(mediaFile)
                )
            )
        }
        for (mediaFile in args.landlordListing.vid) {
            mediaList.add(
                LandlordListingMediaItem(
                    LandlordListingMediaItem.ItemType.VIDEO,
                    Uri.parse(mediaFile)
                )
            )
        }
        mediaAdapter = LandlordListingMediaAdapter(mediaList, requireContext())

        binding.detailedImageGallery.adapter = mediaAdapter
        binding.imageGalleryDots.attachTo(binding.detailedImageGallery)

        // fetch all media for listing
    }

    private fun showUtilitiesGrid() {
        // check which amenities are available and only include those in the card view
        val utilGrid: ArrayList<AmenitiesCard> = ArrayList()

        // drawable resource id map for icons for each amenity
        val utilGridIcons = hashMapOf(
            "Electricity" to R.drawable.utilities_electricity_24,
            "Gas" to R.drawable.utilities_gas_24,
            "Water" to R.drawable.utilities_water_24,
            "Trash Removal" to R.drawable.utilities_trash_24,
            "Snow Removal" to R.drawable.utilities_snow_removal_24
        )

//        val utilList = args.listing.utilities
        for (util in args.landlordListing.utilities) {
            if (util.value) {
                utilGrid.add(
                    AmenitiesCard(
                        utilGridIcons[util.key]!!,
                        util.key.replaceFirstChar { it.uppercase() }
                    )
                )
            }
        }

        val grid = binding.detailedListingUtilities
        grid.adapter = AmenitiesAdapter(
            requireContext(),
            utilGrid
        )
    }

    private fun showAmenitiesGrid() {
        // check which amenities are available and only include those in the card view
        val amenitiesGrid: ArrayList<AmenitiesCard> = ArrayList()

        // drawable resource id map for icons for each amenity
        val amenitiesGridIcons = hashMapOf(
            "Pets Allowed" to R.drawable.amenities_pets_24,
            "In-Unit laundry" to R.drawable.amenities_laundry_24,
            "HVAC System" to R.drawable.amenities_hvac_24,
            "24/7 Security" to R.drawable.amenities_security_24,
            "Gym" to R.drawable.amenities_gym_24,
            "Pool" to R.drawable.amenities_gym_24
        )

        val amenitiesList = args.landlordListing.amenities
        for(key in amenitiesList.keys) {
            Log.i(TAG, key+" ; in keyList="+amenitiesList.containsKey(key))
        }
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
            val listingParcel = args.landlordListing
            binding.detailedListingTitle.text = listingParcel.title
            binding.detailedListingDescription.text = listingParcel.description
            amenitiesList = listingParcel.amenities
            // TODO: populate data for listing to respective view
//            binding.detailedImageGallery.resources = listingParcel.img
//            binding.detailedListingAmenities = listingParel.amenities
        }.root
    }
}