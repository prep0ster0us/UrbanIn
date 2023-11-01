package com.example.urbanin


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.urbanin.roommates.RoommateSearchListViewFragment

class RoommatesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_roommates)

        if (savedInstanceState == null) {
            navigateToRoommateListings()
        }
    }

    private fun navigateToRoommateListings() {
        val fragment = RoommateSearchListViewFragment()
        // Start the fragment transaction
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit() // Apply the transaction
    }
}


