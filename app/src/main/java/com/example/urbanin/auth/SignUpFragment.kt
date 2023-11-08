package com.example.urbanin.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
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
            checkIfEmpty(fieldMap["fname"])
            checkIfEmpty(fieldMap["lname"])
            checkIfEmpty(fieldMap["pwd"])
            checkIfEmpty(fieldMap["confirmPwd"])
        }
    }


    private fun checkIfEmpty(inputView: Pair<TextInputLayout, TextInputEditText>?) {
        if (inputView!!.second.text!!.isEmpty()) {
            inputView.first.error = "Required!"
        } else {
            inputView.first.error = null
        }
    }

    private fun checkIfMatch(
        setPwd: Pair<TextInputLayout, TextInputEditText>,
        confirmPwd: Pair<TextInputLayout, TextInputEditText>
    ) {
        if (setPwd.second.text!! != confirmPwd.second.text!!) {

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
