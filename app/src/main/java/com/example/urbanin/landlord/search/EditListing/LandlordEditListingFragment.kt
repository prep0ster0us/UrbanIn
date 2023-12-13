package com.example.urbanin.landlord.search.EditListing

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.urbanin.MainActivity
import com.example.urbanin.R
import com.example.urbanin.data.ListingData
import com.example.urbanin.data.MediaAdapter
import com.example.urbanin.data.MediaItem
import com.example.urbanin.databinding.FragmentLandlordEditListingBinding
import com.example.urbanin.landlord.search.AddListing.LandlordAddListingFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class LandlordEditListingFragment : Fragment() {

    private lateinit var binding: FragmentLandlordEditListingBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
//    private lateinit var storage: FirebaseStorage
//    private lateinit var storageRef: StorageReference

    private lateinit var saveListing: ListingData

    // to display selected images
    private lateinit var mediaAdapter: MediaAdapter
    private var mediaList: MutableList<MediaItem> = arrayListOf()

    // save uploaded images
    private var listingUriMap: HashMap<Uri, String> = hashMapOf()

    // for camera intent request
    private lateinit var currentMediaPath: String
    private lateinit var imageUri: Uri
    private lateinit var videoUri: Uri

    private val args: LandlordEditListingFragmentArgs by navArgs<LandlordEditListingFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentLandlordEditListingBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
//        storage = FirebaseStorage.getInstance()
//        storageRef = storage.reference

        cleanPriceText(binding.editListingPrice.text.toString())
        setupMediaGallery()
        setupTypeGrid()

        populateListingInformation()

        binding.btnSubmitEditListing.setOnClickListener {
//            binding.uploadProgressBar.isVisible = true
            binding.editListingProgressLayout.isVisible = true
            // TODO: PROCEED WITH UPDATING LISTING IN DATABASE
            with(binding) {
                with(args.landlordEditListing) {
                    saveListing = ListingData(
                        listingID,
                        auth.currentUser!!.uid.toString(),
                        getPropertyType(root),
                        editListingName.text.toString(),
                        binding.editListingDescription.text.toString(),
                        latitude,
                        longitude,
                        editListingAddress.text.toString(),
                        cleanPriceText(editListingPrice.text.toString()),
                        img,
                        vid,
                        LocalDate.now().toString(),
                        availableDisplayText.text.toString(),
                        root.findViewById<Button>(numRoomList.checkedButtonId).text.toString(),
                        root.findViewById<Button>(numBathList.checkedButtonId).text.toString(),
                        root.findViewById<Button>(furnishedList.checkedButtonId).text.toString(),
                        getUtilitiesMap(),
                        getAmenitiesMap()
                    )
                }
            }

            // save this updated listing data in database (to the SAME listing id)
            db.collection("Listings")
                .document(args.landlordEditListing.listingID)
                .set(saveListing)
                .addOnSuccessListener {
                    Log.d(
                        MainActivity.TAG,
                        "Listing added with ID: ${args.landlordEditListing.listingID}"
                    )
                    binding.editListingProgressLayout.visibility = View.GONE
                    findNavController().navigate(LandlordEditListingFragmentDirections.navigateEditListingBackToDetailedListing(saveListing))
                }
                .addOnFailureListener { e ->
                    Log.w(MainActivity.TAG, "Error adding document", e)
                }
        }

        setNavBarVisibility(false)

        // show confirmation dialog when user tries to exit
        binding.btnBackEditListing.setOnClickListener {
            showConfirmExitDialog()
        }
        binding.btnCloseEditListing.setOnClickListener {
            showConfirmExitDialog()
        }
    }

    private fun cleanPriceText(priceText: String): String {
        return priceText.replace("[$, ]".toRegex(),"").trim()
    }

    private fun setNavBarVisibility(flag: Boolean) {
        val parentNavBar: View = requireActivity().findViewById(R.id.bottomNavigationView)
        parentNavBar.isVisible = flag
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

    private fun populateListingInformation() {
        with(binding) {
            val roomMap = linkedMapOf(
                "Studio" to roomStudio.id,
                "1" to room1.id,
                "2" to room2.id,
                "3" to room3.id,
                "4" to room4.id,
                "5" to room5.id
            )
            val bathMap = linkedMapOf(
                "1" to bath1.id,
                "1.5" to bath15.id,
                "2" to bath2.id,
                "3" to bath3.id,
                "4" to bath4.id
            )
            val furnishedMap = linkedMapOf(
                "Full" to furnished1.id,
                "Semi" to furnished2.id,
                "None" to furnished3.id
            )

            with(args.landlordEditListing) {
                editListingName.setText(title)
                editListingAddress.setText(address)
                editListingDescription.setText(description)
                numRoomList.check(roomMap[numRooms]!!)
                numBathList.check(bathMap[numBaths]!!)
                setFilterType(type)
                editListingPrice.setText(formatAsCurrency(price.toFloat()))
                setAmenities(amenities.values)
                setUtilities(utilities.values)
                furnishedList.check(furnishedMap[furnished]!!)
                availableDisplayText.text = availableFrom
            }
        }
    }

    private fun setAmenities(status: Collection<Boolean>) {
        binding.amen1.isChecked = status.elementAt(0)
        binding.amen2.isChecked = status.elementAt(1)
        binding.amen3.isChecked = status.elementAt(2)
        binding.amen4.isChecked = status.elementAt(3)
        binding.amen5.isChecked = status.elementAt(4)
        binding.amen6.isChecked = status.elementAt(5)
    }

    private fun setUtilities(status: Collection<Boolean>) {
        binding.util1.isChecked = status.elementAt(0)
        binding.util2.isChecked = status.elementAt(1)
        binding.util3.isChecked = status.elementAt(2)
        binding.util4.isChecked = status.elementAt(3)
        binding.util5.isChecked = status.elementAt(4)
    }

    private fun setFilterType(type: String) {
        // check if upper grid contains value
        var isFound = false
        for (i in 0 until binding.typeListTop.childCount) {
            val childButton = binding.typeListTop.getChildAt(i) as Button
            if (childButton.text.toString() == type) {
                binding.typeListTop.check(childButton.id)
                isFound = true
            }
        }
        // if not found in the upper grid, check in bottom grid
        if (!isFound) {
            for (i in 0 until binding.typeListBottom.childCount) {
                val childButton = binding.typeListBottom.getChildAt(i) as Button
                if (childButton.text.toString() == type) {
                    binding.typeListBottom.check(childButton.id)
                }
            }
        }
    }

    private fun formatAsCurrency(value: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return "$ " + formatter.format(value).toString()
    }

    private fun getUtilitiesMap(): Map<String, Boolean> {
        return hashMapOf(
            binding.util1.text.toString() to binding.util1.isChecked,
            binding.util2.text.toString() to binding.util2.isChecked,
            binding.util3.text.toString() to binding.util3.isChecked,
            binding.util4.text.toString() to binding.util4.isChecked,
            binding.util5.text.toString() to binding.util5.isChecked,
        )
    }

    private fun getAmenitiesMap(): Map<String, Boolean> {
        return hashMapOf(
            binding.amen1.text.toString() to binding.amen1.isChecked,
            binding.amen2.text.toString() to binding.amen2.isChecked,
            binding.amen3.text.toString() to binding.amen3.isChecked,
            binding.amen4.text.toString() to binding.amen4.isChecked,
            binding.amen5.text.toString() to binding.amen5.isChecked,
            binding.amen6.text.toString() to binding.amen6.isChecked
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_landlord_edit_listing, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.editListingFromStorage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        binding.editListingFromCamera.setOnClickListener {
            // check for camera permissions
            if (checkPermission()) {
                // show dialog to select if user wants to take a photo or video
                pickResourceTypeDialog()
            }
        }
    }


    // **********************************
    // Every function related to media
    // **********************************

    private fun setupMediaGallery() {
//        mediaAdapter = ListingMediaAdapter(args.listing.img, requireContext())
        val mediaList: MutableList<MediaItem> = mutableListOf()
        for (mediaFile in args.landlordEditListing.img) {
            mediaList.add(
                MediaItem(
                    MediaItem.ItemType.IMAGE,
                    Uri.parse(mediaFile)
                )
            )
        }
        for (mediaFile in args.landlordEditListing.vid) {
            mediaList.add(
                MediaItem(
                    MediaItem.ItemType.VIDEO,
                    Uri.parse(mediaFile)
                )
            )
        }
        mediaAdapter = MediaAdapter(mediaList, requireContext(), false)

        binding.editListingMediaLayout.adapter = mediaAdapter
        binding.mediaDotsIndicator.attachTo(binding.editListingMediaLayout)
        binding.mediaDotsIndicator.visibility = if (mediaList.size > 1) {
            View.VISIBLE
        } else {
            View.GONE
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
                Log.e(MainActivity.TAG, "ERROR: ${e.message}")
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
                Log.e(MainActivity.TAG, "ERROR: ${e.message}")
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
                Log.i(MainActivity.TAG, "Permission: Granted")
                // if allowed, display dialog to pick type of resource for camera intent
                pickResourceTypeDialog()
            } else {
                Log.i(MainActivity.TAG, "Permission: Denied")
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
                Log.d(MainActivity.TAG, "Number of items selected: ${uris.size}")
                // add selected image to media view pager
                updateMediaGallery(uris)
            } else {
                Log.d(MainActivity.TAG, "No media selected")
            }
        }

    private fun getMediaType(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        return contentResolver.getType(uri).toString()
    }

    private fun updateMediaGallery(uris: List<Uri>?) {
        if (uris != null) {
            binding.mediaDotsIndicator.isVisible = true
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
            MediaItem(MediaItem.ItemType.IMAGE, uri)
        )
        // refresh view pager
        mediaAdapter.notifyDataSetChanged()
        // slide to newly inserted media file
        binding.editListingMediaLayout.currentItem = mediaAdapter.count
        binding.mediaDotsIndicator.visibility = if (mediaList.size > 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun addVideoToMediaGallery(uri: Uri) {
        mediaList.add(
            MediaItem(MediaItem.ItemType.VIDEO, uri)
        )
        // refresh view pager
        mediaAdapter.notifyDataSetChanged()
        // slide to newly inserted media file
        binding.editListingMediaLayout.currentItem = mediaAdapter.count
        binding.mediaDotsIndicator.visibility = if (mediaList.size > 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
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
                binding.editListingMediaLayout.isVisible = false
            } else {
                Log.d(MainActivity.TAG, "No media selected")
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
            } else {
                Log.d(MainActivity.TAG, "No media selected")
            }
        }

    private fun showConfirmExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Action")
            .setMessage("Any changes made will not be saved. Continue?")
            .setIcon(android.R.drawable.stat_sys_warning)
            .setPositiveButton(
                "Confirm"
            ) { _, _ ->
                findNavController().navigate(
                    LandlordEditListingFragmentDirections.navigateEditListingBackToDetailedListing(args.landlordEditListing)
                )
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->

            }
            .show()
    }
}