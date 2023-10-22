package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.urbanin.databinding.ActivityMainBinding
import com.example.urbanin.databinding.FragmentSplashBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var splashBinding: FragmentSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        splashBinding = FragmentSplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)


    }
}