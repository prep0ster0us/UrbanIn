package com.example.urbanin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.urbanin.databinding.ActivityLandlordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class LandlordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandlordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandlordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val landlordBottomNavBar: BottomNavigationView = binding.bottomNavigationView

        // set navigation component graph for Landlord FragmentContainerView (based on whether the user is logged in)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.landlordFragmentContainerView) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.landlord_bottom_nav_graph)

        // TODO: check if user already logged in, based on that change starting destination for landlord navigation component
        val isLoggedIn = false
        if (isLoggedIn){
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
        
        // set up registerForActivityResult (for photo picker, in "add listing" view)
        setupActivityResult()
    }

    private fun setupActivityResult(): ArrayList<Uri> {
        val galleryImages: ArrayList<Uri> = arrayListOf()
        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                // Callback is invoked after the user selects media items or closes the
                // photo picker.
                if (uris.isNotEmpty()) {
                    Log.d(MainActivity.TAG, "Number of items selected: ${uris.size}")
                    // save selected photos (to add in database when listing finally added)
                    for(imgUri in uris) {
                        galleryImages.add(imgUri);
                    }
                } else {
                    Log.d(MainActivity.TAG, "No media selected")
                }
            }

        return galleryImages
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}