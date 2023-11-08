package com.example.urbanin.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.urbanin.R

public class AccountFragment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_account)

        val tvProfile = findViewById<TextView>(R.id.Profile)
        tvProfile.setOnClickListener {
            navigateToProfile()
        }

        val tvNotifications = findViewById<TextView>(R.id.Notifications)
        tvNotifications.setOnClickListener {
            navigateToNotifications()
        }

        val tvChangePassword = findViewById<TextView>(R.id.ChangePassword)
        tvChangePassword.setOnClickListener {
            navigateToChangePassword()
        }

        val tvSettings = findViewById<TextView>(R.id.Settings)
        tvSettings.setOnClickListener {
            navigateToSettings()
        }

        val tvHelpFAQs = findViewById<TextView>(R.id.HelpFAQs)
        tvHelpFAQs.setOnClickListener {
            navigateToHelpFAQs()
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Implement logout functionality
            finish()
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToNotifications() {
        val intent = Intent(this, NotificationsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToChangePassword() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHelpFAQs() {
        val intent = Intent(this, HelpFAQsActivity::class.java)
        startActivity(intent)
    }

    // Implement other navigation methods for different options
}




