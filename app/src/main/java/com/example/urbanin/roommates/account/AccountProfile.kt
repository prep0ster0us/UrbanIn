package com.example.urbanin.roommates.account

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.urbanin.BuildConfig
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.data.RoommateListingData
import com.example.urbanin.databinding.FragmentAccountProfileBinding
import com.example.urbanin.databinding.RoommateAccountProfileBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AccountProfile : Fragment() {
    private lateinit var binding: RoommateAccountProfileBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var profileListing = RoommateListingData()
    private var listingId: String = ""
    private var profileLocation: LatLng = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RoommateAccountProfileBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // initialize Places, Places API  autocomplete bar does not work without this
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        }
        setupAddressAutocomplete()

        with(binding) {
            btnBackToAccount.setOnClickListener {
               findNavController().popBackStack()
            }
            moveInDisplayText.setOnClickListener {
                showDatePickerDialog()
            }

            // fetch and set details of user in text views
            fillName(auth.currentUser!!.displayName)
            fetchUserInfo(auth.currentUser!!)
        }
    }

    private fun fillName(displayName: String?) {
        if(displayName != null) {
            binding.fnameInput.setText(displayName.split(" ")[0])
            binding.lnameInput.setText(displayName.split(" ")[1])
        }
    }

    private fun fetchUserInfo(user: FirebaseUser) {
        db.collection("Users")
            .document(user.uid)
            .collection("RoommateProfile")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                Log.d(TAG, "Fetch SUCCESS: Details for user- ${user.displayName} : ${user.uid}")

                Log.e(TAG, "${doc.id}:${doc.data}")
                if(doc.data != null) {
                    with(binding) {
                        // save listingID
                        if (doc.data!!["listingID"].toString().isNotEmpty())
                            listingId = doc.data!!["listingID"].toString()
                        fnameInput.setText(doc.data!!["name"].toString().split(" ")[0])
                        lnameInput.setText(doc.data!!["name"].toString().split(" ")[1])
                        ageInput.setText(doc.data!!["age"].toString())
                        budgetInput.setText(doc.data!!["budget"].toString())
                        occupationInput.setText(doc.data!!["occupation"].toString())
                        addressInput.setText(doc.data!!["address"].toString())
                        descriptionInput.setText(doc.data!!["description"].toString())
                        val hobbieList = doc.data!!["hobbies"] as ArrayList<String>
                        hobbieInput.setText(hobbieList.joinToString(separator = ","))
                        moveInDisplayText.text = doc.data!!["moveInDate"].toString()
                        ageInput.setText(doc.data!!["age"].toString())

                        fillGenderGroup(doc.data!!["gender"].toString())
                        fillRoomSizeGroup(doc.data!!["roomSize"].toString())
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Fetch FAILED: Details for user ${user.uid}- $exception")
            }
    }

    private fun fillGenderGroup(gender: String) {
        when(gender) {
            "Male" -> binding.genderList.check(binding.gender1.id)
            "Female" -> binding.genderList.check(binding.gender2.id)
            else -> binding.genderList.check(binding.gender3.id)
        }
    }
    private fun fillRoomSizeGroup(roomSize: String) {
        when(roomSize) {
            "Large" -> binding.roomSizeLayout.check(binding.roomsize1.id)
            else -> binding.roomSizeLayout.check(binding.roomsize2.id)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        with(binding) {
            btnSaveChanges.setOnClickListener {
                if(allDetailsFilledIn()) {
                    profileListing = RoommateListingData(
                        listingId,
                        auth.currentUser!!.uid,
                        ageInput.text.toString().toLong(),
                        "${fnameInput.text.toString()} ${lnameInput.text.toString()}",
                        root.findViewById<Button>(genderList.checkedButtonId).text.toString(),
                        occupationInput.text.toString(),
                        budgetInput.text.toString(),
                        descriptionInput.text.toString(),
                        root.findViewById<Button>(roomSizeLayout.checkedButtonId).text.toString(),
                        profileLocation.latitude.toString(),
                        profileLocation.longitude.toString(),
                        addressInput.text.toString(),
                        auth.currentUser!!.photoUrl.toString(),
                        LocalDate.now().toString(),
                        moveInDisplayText.text.toString(),
                        hobbieInput.text.toString().split(",") as ArrayList<String>
                    )

                    // check if first time update, if yes then add as new roommate listing
                    if (listingId.isEmpty()) {
                        addNewRoommateListing()
                    } else {
                        updateRoommateListing()
                    }

                    // update display name of current user, if any change
                    auth.currentUser!!.updateProfile(
                        userProfileChangeRequest {
                            displayName =
                                "${fnameInput.text.toString()} ${lnameInput.text.toString()}"
                        }
                    )
                    showSuccessDialog()
                }
            }

            btnDiscardChanges.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

    private fun allDetailsFilledIn(): Boolean {
        with(binding) {
            fnameLayout.error = if (fnameInput.text.toString().isEmpty()) "Missing field" else null
            lnameLayout.error = if (lnameInput.text.toString().isEmpty()) "Missing field" else null
            ageLayout.error = if (ageInput.text.toString().isEmpty()) "Missing field" else null
            budgetInput.error = if (budgetInput.text.toString().isEmpty()) "Missing field" else null
            descriptionLayout.error = if (descriptionInput.text.toString().isEmpty()) "Missing field" else null
            occupationInput.error = if (occupationInput.text.toString().isEmpty()) "Missing field" else null
            addressLayout.error = if (addressInput.text.toString().isEmpty() or (profileLocation == LatLng(0.0, 0.0))) "Missing field" else null
            if(genderList.checkedButtonId == View.NO_ID) Toast.makeText(requireContext(), "Please select a gender", Toast.LENGTH_SHORT).show()
            if(roomSizeLayout.checkedButtonId == View.NO_ID) Toast.makeText(requireContext(), "Please select preferred room size", Toast.LENGTH_SHORT).show()
            if(moveInDisplayText.text.isEmpty()) Toast.makeText(requireContext(), "Please select a preferred move-in date", Toast.LENGTH_SHORT).show()
            hobbieLayout.error = if (hobbieInput.text.toString().isEmpty()) "Missing field" else null

            if (fnameInput.text.toString().isEmpty()) return false
            if (lnameInput.text.toString().isEmpty()) return false
            if (ageInput.text.toString().isEmpty()) return false
            if (budgetInput.text.toString().isEmpty()) return false
            if (descriptionInput.text.toString().isEmpty()) return false
            if (occupationInput.text.toString().isEmpty()) return false
            if (addressInput.text.toString().isEmpty()) return false
            if (genderList.checkedButtonId == View.NO_ID) return false
            if (roomSizeLayout.checkedButtonId == View.NO_ID) return false
            if (moveInDisplayText.text.isEmpty()) return false
            if (hobbieInput.text.toString().isEmpty()) return false
        }
        return true
    }

    private fun addNewRoommateListing() {
        val fetch = db.collection("RoommateListings").document()
        profileListing.listingID = fetch.id
        db.collection("RoommateListings")
            .document(fetch.id)
            .set(profileListing)
            .addOnSuccessListener {
                Log.d(TAG, "profile new add SUCCESS")
            }.addOnFailureListener {
                Log.e(TAG, "profile new add FAILED: $it")
            }
        db.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("RoommateProfile")
            .document(auth.currentUser!!.uid)
            .set(profileListing)
    }
    private fun updateRoommateListing() {
        db.collection("RoommateListings")
            .document(profileListing.listingID)
            .set(profileListing)
            .addOnSuccessListener {
                Log.d(TAG, "profile update SUCCESS")
            }.addOnFailureListener {
                Log.e(TAG, "profile update FAILED: $it")
            }
        db.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("RoommateProfile")
            .document(auth.currentUser!!.uid)
            .set(profileListing)
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.success_dialog_layout,null)
        val dialogIcon = view.findViewById<ImageView>(R.id.successIcon)
        Glide.with(requireContext()).load(R.drawable.success_dialog_icon).into(dialogIcon)
        val dialogMsg = view.findViewById<TextView>(R.id.successMessage)
        dialogMsg.text = "Your roommate profile has been updated"
        builder.setView(view)
        builder.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.show()
        // show dialog for 2s
        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisSeconds: Long) {}
            override fun onFinish() {
                builder.dismiss()
                findNavController().popBackStack()
            }
        }
        timer.start()
    }

    private fun setupAddressAutocomplete() {
        binding.addressInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setInitialQuery(binding.addressInput.text.toString())
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
                    binding.addressInput.setText(place.name?.toString())
                    // save coordinates once place selected, since we're not storing them anywhere on the fragment
                    profileLocation = place.latLng!!
                    Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    binding.addressInput.clearFocus()
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User canceled autocomplete")
                binding.addressInput.clearFocus()
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
                val selectedDate: Date? = inputFormat.parse("${monthOfYear + 1}-$dayOfMonth-$selectedYear")
                // Format the selected date into a string
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val formattedDate = selectedDate?.let { dateFormat.format(it) }
                // save selected date from picker in the display text field
                binding.moveInDisplayText.text = formattedDate
            },
            // default selected value in date picker dialog = current date
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}