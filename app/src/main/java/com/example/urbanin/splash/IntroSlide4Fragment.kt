package com.example.urbanin.splash

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.urbanin.MainActivity
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentIntroSlide4Binding

class IntroSlide4Fragment : Fragment() {
    private lateinit var binding: FragmentIntroSlide4Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentIntroSlide4Binding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGetStarted.setOnClickListener{
            // update to state first launch has occured.
            PreferencesManager(requireContext()).setFirstRun()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

}