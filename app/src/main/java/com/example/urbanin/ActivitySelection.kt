package com.example.urbanin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.urbanin.auth.LoginFragment
import com.example.urbanin.databinding.ActivitySelectionBinding

class ActivitySelection : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            roommatesIcon.setOnClickListener {
                navigateToMainContent()
            }

            roommatesText.setOnClickListener {
                navigateToMainContent()
            }

            rentOutIcon.setOnClickListener {
                navigateToLogin()
            }

            rentOutText.setOnClickListener {
                navigateToLogin()
            }
        }
    }

    private fun navigateToMainContent() {
        // Intent to navigate to the Roommates Activity
        val intent = Intent(this, RoommatesActivity::class.java)
        startActivity(intent)
    }



    private fun navigateToLogin() {
        // Navigate to the Login Fragment
        val fragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }
}
