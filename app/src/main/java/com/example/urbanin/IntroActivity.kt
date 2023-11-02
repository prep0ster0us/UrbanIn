package com.example.urbanin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.urbanin.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var introBinding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(introBinding.root)

        Log.d(MainActivity.TAG, "IntroActivity created")
    }
}
