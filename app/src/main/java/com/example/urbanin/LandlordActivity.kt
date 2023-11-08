package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            graph.setStartDestination(R.id.landlordLoginFragment)
            landlordBottomNavBar.isVisible = false
        }

        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)


        // set controller for bottom navigation view
        landlordBottomNavBar.setupWithNavController(navHostFragment.navController)
    }
}