package com.example.urbanin.tenant.search.DetailedListing

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.urbanin.R
import com.example.urbanin.databinding.TenantSearchDetailedListingBinding
import com.example.urbanin.tenant.search.Amenities.AmenitiesAdapter
import com.example.urbanin.tenant.search.Amenities.AmenitiesCard
import com.example.urbanin.tenant.search.DetailedListing.SearchDetailedListingFragmentArgs
import com.google.firebase.firestore.FirebaseFirestore

class SearchDetailedListingFragment : Fragment() {

    private lateinit var binding: TenantSearchDetailedListingBinding

    private lateinit var db: FirebaseFirestore

    private lateinit var amenitiesList: Map<String, Boolean>

    private val args: SearchDetailedListingFragmentArgs by navArgs<SearchDetailedListingFragmentArgs>()

    // for media gallery
    private lateinit var mediaAdapter: ListingMediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TenantSearchDetailedListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        binding.detailedListingTitle.text = args.listing.title
        binding.detailedListingSubTitle.text = args.listing.address

        setupMediaGallery()

        showAmenitiesGrid()
        showUtilitiesGrid()
    }

    private fun setupMediaGallery() {
//        mediaAdapter = ListingMediaAdapter(args.listing.img, requireContext())
        val mediaList: MutableList<ListingMediaItem> = mutableListOf()
        for (mediaFile in args.listing.img) {
            mediaList.add(
                ListingMediaItem(
                    ListingMediaItem.ItemType.VIDEO,
                    Uri.parse(mediaFile)
                )
            )
        }
        mediaAdapter = ListingMediaAdapter(mediaList, requireContext())

        binding.detailedImageGallery.adapter = mediaAdapter
        binding.imageGalleryDots.attachTo(binding.detailedImageGallery)

        // fetch all media for listing
    }

    private fun showUtilitiesGrid() {
        // check which amenities are available and only include those in the card view
        val utilGrid: ArrayList<AmenitiesCard> = ArrayList()

        // drawable resource id map for icons for each amenity
        val utilGridIcons = hashMapOf(
            "electricity" to R.drawable.utilities_electricity_24,
            "gas" to R.drawable.utilities_gas_24,
            "water" to R.drawable.utilities_water_24,
            "trash Removal" to R.drawable.utilities_trash_24,
            "snow Removal" to R.drawable.utilities_snow_removal_24
        )

//        val utilList = args.listing.utilities
        for (util in args.listing.utilities) {
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
            "In-Unit Laundry" to R.drawable.amenities_laundry_24,
            "HVAC System" to R.drawable.amenities_hvac_24,
            "24/7 Security" to R.drawable.amenities_security_24,
            "gym" to R.drawable.amenities_gym_24,
            "pool" to R.drawable.amenities_gym_24
        )

        val amenitiesList = args.listing.amenities
        for (amenity in amenitiesList) {
            if (amenity.value) {
                Toast.makeText(requireContext(), amenity.key, Toast.LENGTH_SHORT).show()
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