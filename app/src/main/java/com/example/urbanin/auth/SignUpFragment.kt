package com.example.urbanin.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class SignUpFragment : Fragment() {

    private lateinit var signUpBinding: FragmentSignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding = FragmentSignUpBinding.inflate(layoutInflater)

        // Configure sign-in to request the user's ID, email address, and basic profile.
        // ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()// Build a GoogleSignInClient with the options specified by gso.

//        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signUpBinding.signUpViewGoogle.setOnClickListener{
            // https://developers.google.com/identity/sign-in/android/sign-in
            // TODO: Fetch click listener code for Google Sign In API
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        updateUI(account)
    }

}