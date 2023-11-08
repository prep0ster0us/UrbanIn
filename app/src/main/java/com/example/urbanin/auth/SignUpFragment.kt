package com.example.urbanin.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var signUpFlag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val fieldMap = hashMapOf<String, Pair<TextInputLayout, TextInputEditText>>(
            "email" to Pair(
                binding.signUpEmailLayout,
                binding.signUpViewEmail
            ),
            "fname" to Pair(
                binding.signUpEmailLayout,
                binding.signUpViewEmail
            ),
            "lname" to Pair(
                binding.signUpEmailLayout,
                binding.signUpViewEmail
            ),
            "pwd" to Pair(
                binding.signUpEmailLayout,
                binding.signUpViewEmail
            ),
            "confirmPwd" to Pair(
                binding.signUpEmailLayout,
                binding.signUpViewEmail
            )
        )

        fieldMap["pwd"]!!.second.doOnTextChanged { text, start, before, count ->
            if (fieldMap["pwd"]!!.second.text!!.isNotEmpty()) {
                if (fieldMap["confirmPwd"]!!.second.text!! != text!!) {
                    fieldMap["pwd"]!!.first.error = "Passwords do not match"
                } else {
                    fieldMap["pwd"]!!.first.error = null
                }
            }
        }
        fieldMap["confirmPwd"]!!.second.doOnTextChanged { text, start, before, count ->
            if (fieldMap["confirmPwd"]!!.second.text!!.isNotEmpty()) {
                if (fieldMap["pwd"]!!.second.text!! != text!!) {
                    fieldMap["confirmPwd"]!!.first.error = "Passwords do not match"
                } else {
                    fieldMap["confirmPwd"]!!.first.error = null
                }
            }
        }

        binding.signUpViewSubmitBtn.setOnClickListener {
            // TODO: check if important fields are empty
            signUpFlag = true
            checkIfEmpty(fieldMap["fname"])
            checkIfEmpty(fieldMap["lname"])
            checkIfEmpty(fieldMap["pwd"])
            checkIfEmpty(fieldMap["confirmPwd"])

            checkIfMatch(fieldMap["pwd"], fieldMap["confirmPwd"])

            // Firebase Authentication - SIGN UP
            if(signUpFlag) {
                auth.createUserWithEmailAndPassword(
                    fieldMap["email"]!!.second.text.toString(),
                    fieldMap["pwd"]!!.second.text.toString()
                ).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        // Add information to database, and sign in the user
                        Log.d(TAG, "Sign Up success!")
                        val user = auth.currentUser
                        // TODO: update UI and navigate to next page/show dialog

                    } else {
                        // if any error while signing up, display error in dialog
                        Log.w(TAG, "Sign up ERROR ", task.exception)
                        // TODO: show dialog and request to try again
                    }
                }
            }

        }
    }


    private fun checkIfEmpty(inputView: Pair<TextInputLayout, TextInputEditText>?) {
        if (inputView!!.second.text!!.isEmpty()) {
            inputView.first.error = "Required!"
            signUpFlag = false
        } else {
            inputView.first.error = null
        }
    }

    private fun checkIfMatch(
        setPwd: Pair<TextInputLayout, TextInputEditText>?,
        confirmPwd: Pair<TextInputLayout, TextInputEditText>?
    ) {
        if (setPwd!!.second.text!! != confirmPwd!!.second.text!!) {
            // TODO: show dialog if passwords don't match on submit
            confirmPwd.first.error = "Password does not match"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure sign-in to request the user's ID, email address, and basic profile.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        binding.signUpViewGoogle.setOnClickListener {
            // TODO: Implement Google Sign-In logic here
        }
    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        // TODO: Update UI accordingly
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
