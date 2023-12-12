package com.example.urbanin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.databinding.ActivityIntroBinding
import com.example.urbanin.splash.PreferencesManager

class IntroActivity : AppCompatActivity() {
    private lateinit var introBinding: ActivityIntroBinding

    private lateinit var prefManager: PreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        introBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(introBinding.root)

        prefManager = PreferencesManager(this)
        if (!prefManager.isFirstRun()) {
            val intent = Intent(this, ActivitySelection::class.java)
            // clear backstack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_left
            )
        }
    }
}
