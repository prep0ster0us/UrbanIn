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
            startActivity(intent)
        }
    }
}
