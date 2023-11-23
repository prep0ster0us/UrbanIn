package com.example.urbanin.landlord.AddListing

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.urbanin.BuildConfig
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.databinding.LandlordFragmentAddListingBinding
import com.example.urbanin.tenant.search.ListingData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale


class LandlordAddListingFragment : Fragment() {
    private lateinit var binding: LandlordFragmentAddListingBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var saveListing: ListingData = ListingData()
    private lateinit var listingLocation: LatLng

    // save uploaded images
    private var galleryImages: ArrayList<Uri>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LandlordFragmentAddListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()
        // fetch id of user logged in (i.e. user creating the listing)
        // 2. userID
//        saveListing.userID = auth.currentUser!!.uid

        // initialize Places, Places API  autocomplete bar does not work without this
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        }

        setupAddressAutocomplete()
        setupAmenitiesGrid()
        setupUtilitiesGrid()
        setupTypeGrid()

        binding.btnSubmitAddListing.setOnClickListener {
            // TODO: check if any required fields haven't been entered

            // if all fields filled in properly, save as "ListingData" object
            // get random document ID for adding the listing (to store within ListingData)
            val fetchLink = db.collection("Listings").document()

            saveListing = ListingData(
                fetchLink.id,
//                auth.currentUser!!.uid.toString(),
                "testUser",
                getPropertyType(binding.root),
                binding.addListingName.text.toString(),
                binding.addListingDescription.text.toString(),
                listingLocation.latitude.toString(),
                listingLocation.longitude.toString(),
                binding.addListingAddress.text.toString(),
                binding.addListingPrice.text.toString(),
//                binding.addListingPrice.text.toString().toLong(),
                // TODO: save images to listing data
                "",
                LocalDate.now().toString(),
                // TODO: add availableFrom date picker layout
//                "",
                LocalDate.now().toString(),
                binding.root.findViewById<Button>(binding.numRoomList.checkedButtonId).text.toString(),
                binding.root.findViewById<Button>(binding.numBathList.checkedButtonId).text.toString(),
                binding.root.findViewById<Button>(binding.furnishedList.checkedButtonId).text.toString(),
                getUtilitiesMap(),
                getAmenitiesMap()
            )

            // save this data in database
            db.collection("Listings")
                .document(saveListing.listingID)
                .set(saveListing)
                .addOnSuccessListener {
                    Log.d(TAG, "Listing added with ID: ${fetchLink.id}")
                    saveListing.listingID = fetchLink.id
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }

        // format rent price as currency
        binding.addListingPrice.setOnFocusChangeListener { _, hasFocus ->
            val currentText = binding.addListingPrice.text.toString()
            if (!hasFocus) {
                val formatter: NumberFormat = DecimalFormat("#,###.##")
                binding.addListingPrice.setText(formatter.format(currentText.toInt()))
            } else {
                if (currentText.isNotEmpty()) {
                    val numberFormat =
                        NumberFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat
                    binding.addListingPrice.setText(numberFormat.parse(currentText)!!.toString())
                }
            }
        }
    }

    private fun getUtilitiesMap(): Map<String, Boolean> {
        return hashMapOf(
            binding.util1.itemText.text.toString() to binding.util1.itemCheckbox.isChecked,
            binding.util2.itemText.text.toString() to binding.util2.itemCheckbox.isChecked,
            binding.util3.itemText.text.toString() to binding.util3.itemCheckbox.isChecked,
            binding.util4.itemText.text.toString() to binding.util4.itemCheckbox.isChecked,
            binding.util5.itemText.text.toString() to binding.util5.itemCheckbox.isChecked,
        )
    }

    private fun getAmenitiesMap(): Map<String, Boolean> {
        return hashMapOf(
            binding.amen1.itemText.text.toString() to binding.amen1.itemCheckbox.isChecked,
            binding.amen2.itemText.text.toString() to binding.amen2.itemCheckbox.isChecked,
            binding.amen3.itemText.text.toString() to binding.amen3.itemCheckbox.isChecked,
            binding.amen4.itemText.text.toString() to binding.amen4.itemCheckbox.isChecked,
            binding.amen5.itemText.text.toString() to binding.amen5.itemCheckbox.isChecked,
            binding.amen6.itemText.text.toString() to binding.amen6.itemCheckbox.isChecked
        )
    }

    private fun getPropertyType(bindingView: View): String {
        return if (binding.typeListTop.checkedButtonId != View.NO_ID) {
            bindingView.findViewById<Button>(binding.typeListTop.checkedButtonId).text.toString()
        } else {
            bindingView.findViewById<Button>(binding.typeListBottom.checkedButtonId).text.toString()
//            binding.typeListBottom[binding.typeListBottom.checkedButtonId].toString()
        }
    }

    private fun setupAddressAutocomplete() {
        binding.addListingAddress.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
                )

                // TODO: build custom dropdown list using "PlacesClient.findAutocompletePredictions()" from Google Places API for autocomplete predictions
                // and display them using "doOnTextChanged"

//            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(Address.this)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setInitialQuery(binding.addListingAddress.text.toString())
                    .build(requireContext())
                startAutocomplete.launch(intent)
            }
        }
    }

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    // store the selected address name (to add to database later)
                    binding.addListingAddress.setText(place.name?.toString())
                    // save coordinates once place selected, since we're not storing them anywhere on the fragment
                    listingLocation = place.latLng!!
//                    saveListing.latitude = place.latLng!!.latitude
//                    saveListing.longitude = place.latLng!!.longitude
                    Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    binding.addListingAddress.clearFocus()
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User canceled autocomplete")
                binding.addListingAddress.clearFocus()
            }
        }

    private fun setupTypeGrid() {
        val topList = binding.typeListTop
        val bottomList = binding.typeListBottom

        binding.typeListTop.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // uncheck selections from bottom grid
            bottomList.uncheck(binding.type4.id)
            bottomList.uncheck(binding.type5.id)
            bottomList.uncheck(binding.type6.id)

            // handle selections from top grid
            if (isChecked) {
                when (checkedId) {
                    binding.type1.id -> topList.check(binding.type1.id)
                    binding.type2.id -> topList.check(binding.type2.id)
                    binding.type3.id -> topList.check(binding.type3.id)
                }
            }
        }

        binding.typeListBottom.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // uncheck selections from top grid
            topList.uncheck(binding.type1.id)
            topList.uncheck(binding.type2.id)
            topList.uncheck(binding.type3.id)

            // handle selections from bottom grid
            if (isChecked) {
                when (checkedId) {
                    binding.type4.id -> bottomList.check(binding.type4.id)
                    binding.type5.id -> bottomList.check(binding.type5.id)
                    binding.type6.id -> bottomList.check(binding.type6.id)
                }
            }
        }
    }

    private fun setupUtilitiesGrid() {
        binding.util1.itemText.text = "Electricity"
        binding.util2.itemText.text = "Gas"
        binding.util3.itemText.text = "Water"
        binding.util4.itemText.text = "Trash Removal"
        binding.util5.itemText.text = "Snow Removal"
    }

    private fun setupAmenitiesGrid() {
        binding.amen1.itemText.text = "Pets Allowed"
        binding.amen2.itemText.text = "In-Unit laundry"
        binding.amen3.itemText.text = "HVAC System"
        binding.amen4.itemText.text = "24/7 Security"
        binding.amen5.itemText.text = "Gym"
        binding.amen6.itemText.text = "Pool"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        // enable user to upload image from gallery

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.addListingFromStorage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
    }

    // Registers a photo picker activity launcher in multi-select mode.
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                Log.d(TAG, "Number of items selected: ${uris.size}")
                // set first selected image as the thumbnail for photos selected
                binding.addListingPhotoGallery.setImageURI(uris[0])
                Toast.makeText(requireContext(), uris.size.toString(), Toast.LENGTH_SHORT).show()
                // save selected photos (to add in database when listing finally added)
//                for (imgUri in uris) {
//                    galleryImages!!.add(imgUri);
//                }
            } else {
                Log.d(TAG, "No media selected")
            }
        }

}