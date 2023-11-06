package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.urbanin.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

//    private var db = FirebaseFirestore.getInstance()
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

        val navView: BottomNavigationView = binding.bottomNavbar

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)
// test
    }

}
