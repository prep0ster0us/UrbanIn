package com.example.urbanin.landlord.AddListing

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.BuildConfig
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.LandlordFragmentAddListingBinding
import com.example.urbanin.landlord.LandlordListingData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale


class LandlordAddListingFragment : Fragment() {
    private lateinit var binding: LandlordFragmentAddListingBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private var saveListing: LandlordListingData = LandlordListingData()
    private lateinit var listingLocation: LatLng

    // to display selected images
    private lateinit var mediaAdapter: MediaPagerAdapter
    private var mediaList: MutableList<MediaPagerItem> = arrayListOf()

    // save uploaded images
    private var listingUriMap: HashMap<Uri, String> = hashMapOf()
//    private var storageMediaList: MutableList<String> = mutableListOf()

    // for camera intent request
    private lateinit var currentMediaPath: String
    private lateinit var imageUri: Uri
    private lateinit var videoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LandlordFragmentAddListingBinding.inflate(layoutInflater)

        // hide bottom nav bar
        val parentNavBar: View = requireActivity().findViewById(R.id.bottomNavigationView)
        parentNavBar.isVisible = false

        mediaAdapter = MediaPagerAdapter(mediaList, requireContext())

        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // initialize Places, Places API  autocomplete bar does not work without this
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        }

        setupAddressAutocomplete()
        setupAmenitiesGrid()
        setupUtilitiesGrid()
        setupTypeGrid()

        binding.btnSubmitAddListing.setOnClickListener {
//            binding.uploadProgressBar.isVisible = true
            binding.addListingProgressLayout.isVisible = true
            // TODO: check if any required fields haven't been entered

            // TODO: IF ALL REQUIRED FIELDS FILLED; PROCEED WITH UPLOADING TO DATABASE
            // get random document ID for adding the listing (to store within ListingData)
            val fetchLink = db.collection("Listings").document()

            // if all fields filled in properly, save as "ListingData" object
            saveListing = LandlordListingData(
                fetchLink.id,
                auth.currentUser!!.uid.toString(),
                getPropertyType(binding.root),
                binding.addListingName.text.toString(),
                binding.addListingDescription.text.toString(),
                listingLocation.latitude.toString(),
                listingLocation.longitude.toString(),
                binding.addListingAddress.text.toString(),
                binding.addListingPrice.text.toString(),
                // save images to listing data
                mutableListOf(),
                mutableListOf(),
                LocalDate.now().toString(),
                // TODO: add date picker for availableFrom
                binding.availableDisplayText.text.toString(),
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
                    // TODO: link listing to user
                    db.collection("Users")
                        .document(auth.currentUser!!.uid)
                        .update("Listings", FieldValue.arrayUnion(saveListing.listingID))
                        .addOnSuccessListener {
                            Log.d(TAG, "User linked to listing: ${saveListing.listingID}")
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "Error linking user and listing: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            // TODO: save media files to firestore storage
            var awaitUpload = 1
            for ((file, type) in listingUriMap) {
                // to check if all files have been uploaded

                val fileName = File(file.path!!).name
                val fileReference = storageRef.child(
                    "Listings/" + saveListing.listingID + "/media/" + fileName + "_" + System.currentTimeMillis()
                        .toString() + "/"
                )
                fileReference.putFile(file)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Media Upload: Success! - " + saveListing.listingID)

                            fileReference.downloadUrl.addOnSuccessListener { uri ->
                                // TODO: add media url to listing (in database for reference)
                                if (type == "image") {
                                    // IMAGES
                                    db.collection("Listings")
                                        .document(saveListing.listingID)
                                        .update("img", FieldValue.arrayUnion(uri.toString()))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Image File added: ${uri.toString()}")
                                            // redirect back to search page, once all files uploaded
                                            if (awaitUpload == listingUriMap.size) {
                                                Log.d(TAG, awaitUpload.toString())
                                                // remove progress bar layout once last media file has been uploaded
                                                binding.addListingProgressLayout.visibility =
                                                    View.GONE
                                                // navigate back to search page, all details have been successfully added
                                                findNavController().navigate(
                                                    LandlordAddListingFragmentDirections.navigateBackToSearchFragment()
                                                )
                                            } else {
                                                awaitUpload += 1
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d(TAG, "Error adding in image uri: ${e.message}")
                                            // redirect back to search page, once all files uploaded
                                            if (awaitUpload == listingUriMap.size) {
                                                // remove progress bar layout once last media file has been uploaded
                                                binding.addListingProgressLayout.visibility =
                                                    View.GONE
                                                findNavController().navigate(
                                                    LandlordAddListingFragmentDirections.navigateBackToSearchFragment()
                                                )
                                            } else {
                                                awaitUpload += 1
                                            }
                                        }
                                } else {
                                    // VIDEOS
                                    db.collection("Listings")
                                        .document(saveListing.listingID)
                                        .update("vid", FieldValue.arrayUnion(uri.toString()))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Video File added: ${uri.toString()}")
                                            // redirect back to search page, once all files uploaded
                                            if (awaitUpload == listingUriMap.size) {
                                                Log.d(TAG, awaitUpload.toString())
                                                // remove progress bar layout once last media file has been uploaded
                                                binding.addListingProgressLayout.visibility =
                                                    View.GONE
                                                findNavController().navigate(
                                                    LandlordAddListingFragmentDirections.navigateBackToSearchFragment()
                                                )
                                            } else {
                                                awaitUpload += 1
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d(TAG, "Error adding in video uri: ${e.message}")
                                            // redirect back to search page, once all files uploaded
                                            if (awaitUpload == listingUriMap.size) {
                                                // remove progress bar layout once last media file has been uploaded
                                                binding.addListingProgressLayout.visibility =
                                                    View.GONE
                                                findNavController().navigate(
                                                    LandlordAddListingFragmentDirections.navigateBackToSearchFragment()
                                                )
                                            } else {
                                                awaitUpload += 1
                                            }
                                        }
                                }
                            }
                        } else {
                            Log.e(TAG, task.exception?.message.toString())
                        }
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                        Log.e(TAG, "Storage upload error: " + exception.message.toString())
                    }
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

        // date picker dialog for availableFrom
        binding.availableDisplayText.setOnClickListener {
            showDatePickerDialog()
        }

        // show confirmation dialog when user tries to exit
        binding.btnBackAddListing.setOnClickListener {
            showConfirmExitDialog()
        }
        binding.btnCloseAddListing.setOnClickListener {
            showConfirmExitDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addListingMediaLayout.adapter = mediaAdapter
        binding.mediaDotsIndicator.attachTo(binding.addListingMediaLayout)
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
        } else if (binding.typeListBottom.checkedButtonId != View.NO_ID) {
            bindingView.findViewById<Button>(binding.typeListBottom.checkedButtonId).text.toString()
        } else {
            ""
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
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.addListingFromStorage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        binding.addListingFromCamera.setOnClickListener {
            // check for camera permissions
            if (checkPermission()) {
                // show dialog to select if user wants to take a photo or video
                pickResourceTypeDialog()
            }
        }
    }

    private fun pickResourceTypeDialog() {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.pick_type_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.findViewById<ImageView>(R.id.cameraPhoto).setOnClickListener {
            dialog.dismiss()
            // create temp uri for the media file (to be taken from camera intent)
            val tempFile = createImageFile()
            try {
                imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.urbanin.mediaFileProvider",
                    tempFile
                )
            } catch (e: Exception) {
                Log.e(TAG, "ERROR: ${e.message}")
            }
            pickCameraImage.launch(imageUri)
        }
        dialog.findViewById<ImageView>(R.id.cameraVideo).setOnClickListener {
            dialog.dismiss()
            // create temp uri for the media file (to be taken from camera intent)
            val tempFile = createVideoFile()
            try {
                videoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.urbanin.mediaFileProvider",
                    tempFile
                )
            } catch (e: Exception) {
                Log.e(TAG, "ERROR: ${e.message}")
            }
            pickCameraVideo.launch(videoUri)
        }
    }

    private fun checkPermission(): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) -> {
                true
            }

            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.CAMERA
                )
                false
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i(TAG, "Permission: Granted")
                // if allowed, display dialog to pick type of resource for camera intent
                pickResourceTypeDialog()
            } else {
                Log.i(TAG, "Permission: Denied")
            }
        }

    private fun createVideoFile(): File {
        val timeStamp = SimpleDateFormat.getDateTimeInstance().toString()
        val videoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            "Listing_${timeStamp}",
            ".mp4",
            videoDir
        ).apply {
            currentMediaPath = absolutePath
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_", Locale.US).format(Date())
        val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "Listing_${timeStamp}",
            ".jpg",
            imageDir
        ).apply {
            currentMediaPath = absolutePath
        }
    }

    // Registers a photo picker activity launcher in multi-select mode.
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                Log.d(TAG, "Number of items selected: ${uris.size}")
                // add selected image to media view pager
                updateMediaGallery(uris)
            } else {
                Log.d(TAG, "No media selected")
            }
        }

    private fun getMediaType(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        return contentResolver.getType(uri).toString()
    }

    private fun updateMediaGallery(uris: List<Uri>?) {
        if (uris != null) {
            binding.mediaDotsIndicator.isVisible = true
            binding.addListingTemplatePhoto.isVisible = false
            for (uri in uris) {
                // save selected photos (to add in database when listing finally added)
//                listingUriMap.add(uri);

                // add media to media gallery (based on media type)
                val mediaType = getMediaType(uri)
                if (mediaType.startsWith("image")) {
                    listingUriMap[uri] = "image"
                    addImageToMediaGallery(uri)
                } else if (mediaType.startsWith("video")) {
                    listingUriMap[uri] = "video"
                    addVideoToMediaGallery(uri)
                }
            }
        }
    }

    private fun addImageToMediaGallery(uri: Uri) {
        mediaList.add(
            MediaPagerItem(MediaPagerItem.ItemType.IMAGE, uri)
        )
        // refresh view pager
        mediaAdapter.notifyDataSetChanged()
        // slide to newly inserted media file
        binding.addListingMediaLayout.currentItem = mediaAdapter.count;
    }

    private fun addVideoToMediaGallery(uri: Uri) {
        mediaList.add(
            MediaPagerItem(MediaPagerItem.ItemType.VIDEO, uri)
        )
        mediaList
        // refresh view pager
        mediaAdapter.notifyDataSetChanged()
        // slide to newly inserted media file
        binding.addListingMediaLayout.currentItem = mediaAdapter.count;
    }

    // Registers a camera launcher (picture mode).
    private val pickCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            // Callback is invoked after the user clicks and saves an image or closes the camera intent.
            if (success) {
                // TODO: add view pager (with horizontal scroll) and add all images to this list (taken from camera + selected from storage)
                addImageToMediaGallery(imageUri)
                // save media uri (to be uploaded to database)
                listingUriMap[imageUri] = "image"
                binding.mediaDotsIndicator.isVisible = true
                binding.addListingTemplatePhoto.isVisible = false
            } else {
                Log.d(TAG, "No media selected")
            }
        }

    // Registers a camera launcher (video mode).
    private val pickCameraVideo =
        registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            // Callback is invoked after the user clicks and saves an image or closes the camera intent.
            if (success) {
                // TODO: add view pager (with horizontal scroll) and add all images to this list (taken from camera + selected from storage)
                addVideoToMediaGallery(videoUri)
                // save media uri (to be uploaded to database)
                listingUriMap[videoUri] = "video"
                binding.mediaDotsIndicator.isVisible = true
                binding.addListingTemplatePhoto.isVisible = false
            } else {
                Log.d(TAG, "No media selected")
            }
        }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            requireContext(),
            { _, selectedYear, monthOfYear, dayOfMonth ->
                // Set the selected date using the values received from the DatePicker dialog
                val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)
                val selectedDate: Date? =
                    inputFormat.parse("${monthOfYear + 1}-$dayOfMonth-$selectedYear")
                // Format the selected date into a string
                val dateFormat =
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.US)
                val formattedDate = selectedDate?.let { dateFormat.format(it) }
                // save selected date from picker in the display text field
                binding.availableDisplayText.text = formattedDate
            },
            // default selected value in date picker dialog = current date
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showConfirmExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Action")
            .setMessage("You will lose any changes you've made. Continue?")
            .setIcon(android.R.drawable.stat_sys_warning)
            .setPositiveButton(
                "Confirm"
            ) { _, _ ->
                findNavController().navigate(
                    LandlordAddListingFragmentDirections.navigateBackToSearchFragment()
                )
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->

            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        // hide main bottom nav bar
        setNavBarVisibility(false)
    }

    private fun setNavBarVisibility(flag: Boolean) {
        val parentNavBar: View = requireActivity().findViewById(R.id.bottomNavigationView)
        parentNavBar.isVisible = flag
    }
}