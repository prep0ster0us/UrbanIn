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

        // navigate to last known user mode
        when(prefManager.getUserMode()) {
            "roommate" -> binding.roommatesLayout.callOnClick()
            "tenant" -> binding.rentPlaceLayout.callOnClick()
            "landlord" -> binding.rentOutLayout.callOnClick()
        }

        with(binding) {
            roommatesLayout.setOnClickListener {
                prefManager.setUserMode("roommate")
                navigateToRoommatesActivity()
            }
            rentPlaceLayout.setOnClickListener {
                prefManager.setUserMode("tenant")
                navigateToTenantActivity()
            }
            rentOutLayout.setOnClickListener {
                prefManager.setUserMode("landlord")
                prefManager.setRedirectContext("Mode selection")
                navigateToLogin()
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
