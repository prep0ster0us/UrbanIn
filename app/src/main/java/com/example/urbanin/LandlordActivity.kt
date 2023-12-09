package com.example.urbanin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.urbanin.auth.LoginFragmentDirections
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.databinding.ActivityLandlordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.urbanin.MainActivity.Companion.TAG

class LandlordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandlordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var prefManager: LoginPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandlordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        prefManager = LoginPreferenceManager(this)

        val landlordBottomNavBar: BottomNavigationView = binding.bottomNavigationView

        // set navigation component graph for Landlord FragmentContainerView (based on whether the user is logged in)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.landlordFragmentContainerView) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.landlord_bottom_nav_graph)

        // TODO: check if user already logged in, based on that change starting destination for landlord navigation component
        if (prefManager.isLoggedIn()){
            setCurrentLoggedInUser()
            graph.setStartDestination(R.id.landlordSearchFragment)
            landlordBottomNavBar.isVisible = true
        }else {
            graph.setStartDestination(R.id.loginFragment)
            landlordBottomNavBar.isVisible = false
        }

        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)

        // set controller for bottom navigation view
        landlordBottomNavBar.setupWithNavController(navHostFragment.navController)
    }

    private fun setCurrentLoggedInUser() {
        val (email, pwd) = prefManager.fetchLoginCreds()
        auth.signInWithEmailAndPassword(email.toString(),pwd.toString())
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "ALREADY LOGGED IN - signInWithEmail:success")
                    Log.i(TAG, "LOG IN SUCCESS: User = $email")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure ", task.exception)
                }

            }
    }

}