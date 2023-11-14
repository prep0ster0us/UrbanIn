package com.example.urbanin.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.urbanin.LandlordActivity
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isLoggedIn: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentLoginBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        isLoggedIn = (auth.currentUser != null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val forgotPasswordButton = view.findViewById<TextView>(R.id.loginViewForgotPwd)
        forgotPasswordButton.setOnClickListener {
            findNavController().navigate(R.id.navigate_landlord_login_to_forgot_pwd)
        }


        binding.loginViewSubmitBtn.setOnClickListener {
            if (checkIfUserExists()) {
                // check if credentials match
                // TODO: use co-routine or await to ensure async processing
                auth.signInWithEmailAndPassword(
                    binding.loginViewUsnField.text.toString(),
                    binding.loginViewPwdField.text.toString()
                ).addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        // navigate successfully to next page
                        // TODO: decide next page based on context of fragment trigger
                        Toast.makeText(
                            requireContext(),
                            "Welcome, ${auth.currentUser!!.displayName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginSuccessToSearch())
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure ", task.exception)
                        // clear password field
                        binding.loginViewPwdField.setText("")
                        // TODO: show error dialog to indicate wrong credentials
                        Toast.makeText(requireContext(), "Wrong Credentials! Try again..", Toast.LENGTH_SHORT).show()
                    }
                }

                // Navigate to LandlordFragment
//                val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
//                val navController = navHostFragment.navController
//                navController.navigate(R.id.navigate_login_to_landlord_search)
            } else {
                // navigate to sign up page
                findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginToSignUp())
            }
        }



        binding.loginViewSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.navigateLandlordLoginToSignUp())
//                requireActivity().findNavController(R.id.landlordFragmentContainerView).navigate(R.id.navigate_landlord_login_to_sign_up)
            // Navigate to LandlordFragment
//                val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
//                val navController = navHostFragment.navController
//                navController.navigate(R.id.navigate_login_to_sign_up)
        }
    }



    private fun checkIfUserExists(): Boolean {
        var isRegistered = false
        db.collection("Users")
            .whereEqualTo("Email", binding.loginViewUsnField.text.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "User exists: ${document.id}")
                    // TODO: proceed with login if user exists and match credentials
                    isRegistered = true
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "User not registered!")
            }
        Log.d(TAG, isRegistered.toString())
        return isRegistered
        /*
        var isRegistered: Boolean = false
        db.collection("Users")
            .whereEqualTo("Email", binding.loginViewUsnField.text.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "User exists: ${document.id}")
                    // TODO: proceed with login if user exists and match credentials
                    isRegistered = true
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "User not registered!")
            }
        return isRegistered
        */
    }
}
