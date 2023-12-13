package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.urbanin.databinding.ActivityRoommatesBinding
import com.example.urbanin.roommates.search.RoommateSearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class RoommatesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoommatesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.roommateBottomBar

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.roommateFCV) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

    }
}


