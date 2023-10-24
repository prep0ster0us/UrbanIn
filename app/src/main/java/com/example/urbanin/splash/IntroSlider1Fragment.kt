package com.example.urbanin.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.urbanin.databinding.FragmentIntroSlider1Binding

class IntroSlider1Fragment : Fragment() {
    private lateinit var binding: FragmentIntroSlider1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIntroSlider1Binding.inflate(layoutInflater)
        return binding.root
    }

}