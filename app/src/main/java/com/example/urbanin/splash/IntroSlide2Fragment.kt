package com.example.urbanin.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentIntroSlider2Binding

class IntroSlide2Fragment : Fragment() {
    private lateinit var binding: FragmentIntroSlider2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentIntroSlider2Binding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

}