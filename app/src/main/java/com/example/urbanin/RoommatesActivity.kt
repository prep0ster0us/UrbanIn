package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.urbanin.roommates.RoommateListingsFragment

class RoommatesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roommates)

        if (savedInstanceState == null) {
            navigateToRoommateListings()
        }
    }

    private fun navigateToRoommateListings() {
        val fragment = RoommateListingsFragment() // Ensure this is the correct fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}


