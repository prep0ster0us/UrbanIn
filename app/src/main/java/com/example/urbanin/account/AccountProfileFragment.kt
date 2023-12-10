package com.example.urbanin.account

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentAccountProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class AccountProfileFragment : Fragment() {
    private lateinit var binding: FragmentAccountProfileBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var infoMap: LinkedHashMap<String, String> = linkedMapOf(
        "fname" to "",
        "lname" to "",
        "email" to "",
        "phone" to ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAccountProfileBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        with(binding) {
            btnBackToAccount.setOnClickListener {
                val action = AccountProfileFragmentDirections.navigateProfileBackToAccount()
                findNavController().navigate(action)
            }
            // fetch and set details of user in text views
            fetchUserInfo(auth.currentUser!!)
        }
    }

    private fun enableSubmitBtns(flag: Boolean) {
        with(binding) {
            btnSaveChanges.isVisible = flag
            btnDiscardChanges.isVisible = flag
        }
    }

    private fun fetchUserInfo(user: FirebaseUser) {
        db.collection("Users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                Log.d(TAG, "Fetch SUCCESS: Details for user- ${user.displayName} : ${user.uid}")
                binding.fnameInput.setText(doc.data!!["First Name"].toString())
                binding.lnameInput.setText(doc.data!!["Last Name"].toString())
                binding.emailInput.setText(doc.data!!["Email"].toString())
                binding.phoneInput.setText(doc.data!!["Phone"].toString())
                // save to hash map (to keep track of any changes)
                infoMap["fname"] = doc.data!!["First Name"].toString()
                infoMap["lname"] = doc.data!!["Last Name"].toString()
                infoMap["email"] = doc.data!!["Email"].toString()
                infoMap["phone"] = doc.data!!["Phone"].toString()

                //add text change listeners
                with(binding) {
                    fnameInput.doOnTextChanged { _, _, _, _ ->
                        enableSubmitBtns(fnameInput.text.toString() != infoMap["fname"])
                    }
                    lnameInput.doOnTextChanged { _, _, _, _ ->
                        enableSubmitBtns(lnameInput.text.toString() != infoMap["lname"])
                    }
                    emailInput.doOnTextChanged { _, _, _, _ ->
                        enableSubmitBtns(emailInput.text.toString() != infoMap["email"])
                    }
                    phoneInput.doOnTextChanged { _, _, _, _ ->
                        enableSubmitBtns(phoneInput.text.toString() != infoMap["phone"])
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Fetch FAILED: Details for user ${user.uid}- $exception")
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        with(binding) {
            btnSaveChanges.setOnClickListener {
                // save updated details in Firestore
                db.collection("Users")
                    .document(auth.currentUser!!.uid)
                    .update(
                        "First Name", fnameInput.text.toString(),
                        "Last Name", lnameInput.text.toString(),
                        "Email", emailInput.text.toString(),
                        "Phone", phoneInput.text.toString()
                    )
                    .addOnSuccessListener {
                        Log.d(TAG, "User ${auth.currentUser!!.uid}: Details updated successfully!")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            TAG,
                            "User ${auth.currentUser!!.uid}: Details couldn't be updated- $exception"
                        )
                    }
                // update display name of current user, if any change
                auth.currentUser!!.updateProfile(
                    userProfileChangeRequest {
                        displayName = "${fnameInput.text.toString()} ${lnameInput.text.toString()}"
                    }
                )
                showSuccessDialog()
            }

            btnDiscardChanges.setOnClickListener {
                findNavController().navigate(AccountProfileFragmentDirections.navigateProfileBackToAccount())
            }
        }
        return binding.root
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.success_dialog_layout,null)
        val dialogIcon = view.findViewById<ImageView>(R.id.successIcon)
        Glide.with(requireContext()).load(R.drawable.success_dialog_icon).into(dialogIcon)
        val dialogMsg = view.findViewById<TextView>(R.id.successMessage)
        dialogMsg.text = "Your account details have been updated"
        builder.setView(view)
        builder.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.show()
        // show dialog for 2s
        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisSeconds: Long) {}
            override fun onFinish() {
                builder.dismiss()
                findNavController().navigate(AccountProfileFragmentDirections.navigateProfileBackToAccount())
            }
        }
        timer.start()
    }

}