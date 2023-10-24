package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.urbanin.databinding.ActivityMainBinding
import com.example.urbanin.databinding.FragmentSplashBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var splashBinding: FragmentSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // to add (and customize) the default splash screen provided within android
        var keepSplashOnScreen = true       // show the splash screen by default
        //
        // TODO: Update this condition to reflect when everything (to be shown on screen) is ready in the background OR data fetched from firestore
        //
        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
        Handler(Looper.getMainLooper()).postDelayed({
            keepSplashOnScreen = false
        }, 3000)

        
//        binding = ActivityMainBinding.inflate(layoutInflater)
        splashBinding = FragmentSplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

    }
}