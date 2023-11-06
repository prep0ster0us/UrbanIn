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
            roommatesLayout.setOnClickListener {
                navigateToRoommatesActivity()
            }
            rentPlaceLayout.setOnClickListener {
                navigateToTenantActivity()
            }
            rentOutLayout.setOnClickListener {
                navigateToLogin()
            }
        }
    }

    private fun navigateToTenantActivity() {
        // Intent to navigate to the Tenant (Main) Activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_left
        );
    }

    private fun navigateToRoommatesActivity() {
        // Intent to navigate to the Roommates Activity
        val intent = Intent(this, RoommatesActivity::class.java)
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_left
        );
    }


    private fun navigateToLogin() {
        /*
        // Navigate to the Login Fragment
        val fragment = LoginFragment()
        supportFragmentManager.beginTransaction().replace(android.R.id.content, fragment)
            .addToBackStack(null).commit()
        */

        // Intent to navigate to the Roommates Activity
        val intent = Intent(this, LandlordActivity::class.java)
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_left
        );
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_right
        );
    }
}
