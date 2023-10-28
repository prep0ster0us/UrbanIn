package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.urbanin.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()

    companion object {
        var TAG = "UrbanIn-Debug"
    }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        // to add (and customize) the default splash screen provided within android
//        var keepSplashOnScreen = true       // show the splash screen by default
//        //
//        // TODO: Update this condition to reflect when everything (to be shown on screen) is ready in the background OR data fetched from firestore
//        //
//        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
//        Handler(Looper.getMainLooper()).postDelayed({
//            keepSplashOnScreen = false
//        }, 1000)


        Log.d(TAG, "main activity launched")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}