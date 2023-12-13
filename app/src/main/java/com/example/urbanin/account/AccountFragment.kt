package com.example.urbanin.account

import android.app.Dialog
import android.content.Intent
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
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.ActivitySelection
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.userListingCollection
import com.example.urbanin.databinding.FragmentAccountBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var prefManager: LoginPreferenceManager

    // for uploading/updating profile image
    private lateinit var currentMediaPath: String
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAccountBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        prefManager = LoginPreferenceManager(requireContext())

        binding.btnLogout.text = if (prefManager.isLoggedIn()) "LOGOUT" else "SIGN IN"

        // check if logged in
        if (prefManager.isLoggedIn()) {
            with(binding) {
                toggleViewVisibility(true)
                // set text based on logged in state
                profileUserName.text = auth.currentUser!!.displayName
                // check if logged in user has a corresponding profile image
                checkHasProfileImage(auth.currentUser!!)
                ProfileImage.setOnClickListener {
                    // check for camera permissions
                    if (checkPermission()) {
                        // show dialog to select if user wants to take a photo or video
                        pickResourceTypeDialog()
                    }
                }
                btnLogout.text = "LOGOUT"
                btnLogout.setOnClickListener {
                    if (prefManager.isLoggedIn()) {
                        // save logged out state in SharedPreferences
                        prefManager.setLoggedIn(false)
                        auth.signOut()
                        Snackbar.make(
                            root,
                            "Logged out successfully!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    findNavController().navigate(AccountFragmentDirections.navigateAccountToLogin())
                }
            }
        } else {
            with(binding) {
                toggleViewVisibility(false)
                profileUserName.text = "Guest User"
                with(btnLogout) {
                    text = "SIGN IN"
                    setOnClickListener {
                        navigateToLogin()
                    }
                }
            }


        }
        binding.btnLogout.setOnClickListener {
            if (prefManager.isLoggedIn()) {
                auth.signOut()
                // save logged out state in SharedPreferences
                prefManager.setLoggedIn(false)
                Snackbar.make(binding.root, "Logged out successfully!", Snackbar.LENGTH_SHORT)
                    .show()
                // clear landlord listings
                userListingCollection = arrayListOf()
            }
            navigateToLogin()
        }

        with(binding) {
            switchMode.setOnClickListener {
                val intent = Intent(requireContext(), ActivitySelection::class.java)
                // clear backstack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            Settings.setOnClickListener {
                val action = AccountFragmentDirections.navigateAccountToSettings()
                findNavController().navigate(action)
            }
            Profile.setOnClickListener {
                val action = AccountFragmentDirections.navigateAccountToProfile()
                findNavController().navigate(action)
            }
            ChangePassword.setOnClickListener {
                val action = AccountFragmentDirections.navigateAccountToChangePwd()
                findNavController().navigate(action)
            }
        }

    }

    private fun toggleViewVisibility(flag: Boolean) {
        with(binding) {
            EditIcon.isVisible = flag
            Profile.isVisible = flag
            ChangePassword.isVisible = flag
        }
    }

    private fun navigateToLogin() {
        val redirectContext = if(prefManager.getUserMode() == "tenant") "tenant_account" else "landlord_account"
        prefManager.setRedirectContext(redirectContext)
        findNavController().navigate(AccountFragmentDirections.navigateAccountToLogin())
    }

    private fun checkHasProfileImage(user: FirebaseUser) {
        if(user.photoUrl != null) {
            Picasso.get().load(user.photoUrl).into(binding.ProfileImage)
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

    private fun pickResourceTypeDialog() {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.pick_resource_location_dialog)
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
        dialog.findViewById<ImageView>(R.id.galleryPhoto).setOnClickListener {
            dialog.dismiss()
            pickFromGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_", Locale.US).format(Date())
        val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "User_${auth.currentUser!!.email}_${timeStamp}",
            ".png",
            imageDir
        ).apply {
            currentMediaPath = absolutePath
        }
    }

    // Registers a camera launcher (picture mode).
    private val pickCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            // Callback is invoked after the user clicks and saves an image or closes the camera intent.
            if (success) {
                binding.ProfileImage.setImageURI(imageUri)
                // upload profile image to storage
                uploadToFirebaseStorage(imageUri)

            } else {
                Log.d(TAG, "No media selected")
            }
        }
    // Registers a photo picker activity launcher in single-select mode.
    private val pickFromGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects media items
            // or closes the photo picker.
            if (uri != null) {
                Log.d(TAG, "Profile Image selected: $uri")
                // add profile image to view
                binding.ProfileImage.setImageURI(uri)
                uploadToFirebaseStorage(uri)
            } else {
                Log.d(TAG, "No profile image selected")
            }
        }

    private fun uploadToFirebaseStorage(imageUri: Uri) {
        val fileName = File(imageUri.path!!).name
        val fileReference =
            storageRef.child("Users/${auth.currentUser!!.email}/profileImage.png")
        fileReference.putFile(imageUri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(
                        TAG,
                        "Profile Image Upload: Success! - User: name= ${auth.currentUser!!.displayName} with email= ${auth.currentUser!!.email}"
                    )
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        Log.d(TAG, "Profile Image File added: $uri")
                        auth.currentUser!!.updateProfile(
                            userProfileChangeRequest {
                                photoUri = uri
                            }
                        )
                        // save profile image to database, for that user
                        db.collection("Users")
                            .document(auth.currentUser!!.uid)
                            .update("profileImage",uri.toString())
                            .addOnSuccessListener {
                                Log.d(TAG, "Saved profile image to Users collection")
                            }.addOnFailureListener { exception ->
                                Log.e(TAG, "Couldn't save image to Users collection- $exception")
                            }
                    }
                } else {
                    Log.e(
                        TAG,
                        "Profile Image Upload: Failed! - ${task.exception?.message.toString()}"
                    )
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Log.e(TAG, "Storage upload error: " + exception.message.toString())
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

}