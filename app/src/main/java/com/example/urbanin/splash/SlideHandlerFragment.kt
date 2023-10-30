package com.example.urbanin.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.urbanin.databinding.FragmentSlideHandlerBinding

class SlideHandlerFragment : Fragment() {

    private lateinit var sliderBinding: FragmentSlideHandlerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sliderBinding = FragmentSlideHandlerBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_slide_handler, container, false)

        val fragmentList = arrayListOf<Fragment>(
            IntroSlide1Fragment(),
            IntroSlide2Fragment(),
            IntroSlide3Fragment(),
            IntroSlide4Fragment()
        )

        val sliderAdapter = SliderViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = sliderBinding.sliderFragmentViewPager
        viewPager.adapter = sliderAdapter

        // attach view pager adapter to dots indicator view
        val indicator = sliderBinding.dotsIndicator
        indicator.attachTo(viewPager)

        return sliderBinding.root
    }

}