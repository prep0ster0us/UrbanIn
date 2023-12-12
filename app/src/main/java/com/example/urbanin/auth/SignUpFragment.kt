package com.example.urbanin.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.urbanin.R
import com.example.urbanin.account.AccountProfileFragmentDirections
import com.example.urbanin.data.ListingData
import com.example.urbanin.landlord.search.AddListing.LandlordAddListingFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private var signUpFlag = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        val fieldMap = hashMapOf(
            "email" to Pair(binding.signUpEmailLayout, binding.signUpViewEmail),
            "fname" to Pair(binding.signUpFnameLayout, binding.signUpViewFname),
            "lname" to Pair(binding.signUpLnameLayout, binding.signUpViewLname),
            "pwd" to Pair(binding.signUpPwdLayout, binding.signUpViewPwd),
            "confirmPwd" to Pair(binding.signUpConfirmPwdLayout, binding.signUpViewConfirmPwd),
            "phone" to Pair(binding.signUpPhoneLayout, binding.signUpViewPhone)
        )

        setUpPasswordValidation(fieldMap["pwd"]!!.first, fieldMap["pwd"]!!.second)

        binding.signUpViewSubmitBtn.setOnClickListener {
            signUpFlag = true
            fieldMap.forEach { (_, value) -> checkIfEmpty(value) }
            checkIfMatch(fieldMap["pwd"], fieldMap["confirmPwd"])

            if (signUpFlag) {
                performFirebaseSignUp(fieldMap)
            }
        }

        binding.signUpViewGoogle.setOnClickListener {
            googleSignIn()
        }

        binding.backToLoginButton.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.navigateSignUpToLogin())
        }
        return binding.root
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun setUpPasswordValidation(
        passwordLayout: TextInputLayout,
        passwordEditText: TextInputEditText
    ) {
        passwordEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                passwordLayout.error = "Password required"
                signUpFlag = false
            } else if (text.length < 6) {
                passwordLayout.error = "Minimum 6 characters required"
                signUpFlag = false
            } else if (!text.matches(".*[A-Z].*".toRegex())) {
                passwordLayout.error = "Must contain at least 1 uppercase letter"
                signUpFlag = false
            } else if (!text.matches(".*[0-9].*".toRegex())) {
                passwordLayout.error = "Must contain at least 1 number"
                signUpFlag = false
            } else {
                passwordLayout.error = null
            }
        }
    }

    private fun performFirebaseSignUp(fieldMap: HashMap<String, Pair<TextInputLayout, TextInputEditText>>) {
        auth.createUserWithEmailAndPassword(
            fieldMap["email"]!!.second.text.toString(),
            fieldMap["pwd"]!!.second.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                handleSuccessfulSignUp(fieldMap)
            } else {
                if (task.exception is FirebaseAuthUserCollisionException) {
                    showUserCollisionDialog()
                } else {
                    showSignUpError(task.exception?.message)
                }
            }
        }
    }

    private fun handleSuccessfulSignUp(fieldMap: HashMap<String, Pair<TextInputLayout, TextInputEditText>>) {
        val user = auth.currentUser
        // Set display name using UserProfileChangeRequest
        val addDisplayName = UserProfileChangeRequest.Builder()
            .setDisplayName("${fieldMap["fname"]!!.second.text} ${fieldMap["lname"]!!.second.text}")
            .build()
        user?.updateProfile(addDisplayName)
            ?.addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    Log.d(TAG, "set display name: ${fieldMap["fname"]!!.second.text}")
                } else {
                    Log.e(TAG, "Error setting display name with user: ${updateTask.exception}")
                }
            }
        val userDetails = hashMapOf(
            "First Name" to fieldMap["fname"]!!.second.text.toString(),
            "Last Name" to fieldMap["lname"]!!.second.text.toString(),
            "Email" to fieldMap["email"]!!.second.text.toString(),
            "Phone" to fieldMap["phone"]!!.second.text.toString(),
            "Listings" to arrayListOf<ListingData>(),
            "profileImage" to ""
        )
        db.collection("Users")
            .document(user!!.uid)
            .set(userDetails)
            .addOnSuccessListener {
                showSuccessDialog()
                clearInputFields(fieldMap)
            }
            .addOnFailureListener { e ->
                showSignUpError(e.message)
            }
    }

    private fun showSuccessDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.success_dialog_layout, null)
        val dialogIcon = view.findViewById<ImageView>(R.id.successIcon)
        Glide.with(requireContext()).load(R.drawable.success_dialog_icon).into(dialogIcon)
        val dialogMsg = view.findViewById<TextView>(R.id.successMessage)
        dialogMsg.text = "Password updated"
        builder.setView(view)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        builder.show()
        // show dialog for 3s
        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisSeconds: Long) {}
            override fun onFinish() {
                builder.dismiss()
                findNavController().navigate(SignUpFragmentDirections.navigateSignUpToLogin())

            }
        }
        timer.start()

    }

    private fun clearInputFields(fieldMap: HashMap<String, Pair<TextInputLayout, TextInputEditText>>) {
        fieldMap.values.forEach { it.second.text?.clear() }
    }

    private fun showUserCollisionDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Sign In Failed")
            setMessage("User already exists")
            setPositiveButton("OK", null)
            setIcon(R.drawable.ic_error_outline)
            setCancelable(true)
            show()
        }
    }

    private fun showSignUpError(errorMessage: String?) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Sign Up Error")
            setMessage(errorMessage ?: "An unknown error occurred")
            setPositiveButton("OK", null)
            setCancelable(true)
            show()
        }
    }

    private fun checkIfEmpty(inputView: Pair<TextInputLayout, TextInputEditText>?) {
        if (inputView?.second?.text.isNullOrEmpty()) {
            inputView?.first?.error = "This field is required"
            signUpFlag = false
        } else {
            inputView?.first?.error = null
        }
    }

    private fun checkIfMatch(
        setPwd: Pair<TextInputLayout, TextInputEditText>?,
        confirmPwd: Pair<TextInputLayout, TextInputEditText>?
    ) {
        if (setPwd?.second?.text.toString() != confirmPwd?.second?.text.toString()) {
            confirmPwd?.first?.error = "Passwords do not match"
            signUpFlag = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Successfully signed in
            // TODO: Perform after sign-in logic here (e.g., store user info in Firebase)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            // Handle sign-in failure
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}

