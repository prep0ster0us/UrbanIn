package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.urbanin.roommates.SearchListViewFragment

class RoommatesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_roommates)

        if (savedInstanceState == null) {
            navigateToRoommateListings()
        }
    }

    private fun navigateToRoommateListings() {
        val fragment = SearchListViewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}

