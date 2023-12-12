package com.example.urbanin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.databinding.ActivitySelectionBinding

class ActivitySelection : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var prefManager: LoginPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectionBinding.inflate(layoutInflater)

        prefManager = LoginPreferenceManager(this)
        setContentView(binding.root)

        with(binding) {
            roommatesLayout.setOnClickListener {
                navigateToRoommatesActivity()
                prefManager.setUserMode("roommate")
            }
            rentPlaceLayout.setOnClickListener {
                prefManager.setUserMode("tenant")
                navigateToTenantActivity()
            }
            rentOutLayout.setOnClickListener {
                prefManager.setRedirectContext("Mode selection")
                navigateToLogin()
                prefManager.setUserMode("landlord")
            }
        }
    }

    private fun navigateToTenantActivity() {
        // Intent to navigate to the Tenant (Main) Activity
        val intent = Intent(this, MainActivity::class.java)
        // clear backstack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_left
        );
    }

    private fun navigateToRoommatesActivity() {
        // Intent to navigate to the Roommates Activity
        val intent = Intent(this, RoommatesActivity::class.java)
        // clear backstack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_left
        );
    }


    private fun navigateToLogin() {
        // Intent to navigate to the Landlord Activity
        val intent = Intent(this, LandlordActivity::class.java)
        // clear backstack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
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
