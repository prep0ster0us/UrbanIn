package com.example.urbanin.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.urbanin.R
import com.example.urbanin.databinding.FragmentSlideHandlerBinding

class SlideHandlerFragment : Fragment(R.layout.fragment_slide_handler) {

    private var _sliderBinding: FragmentSlideHandlerBinding? = null
    private val sliderBinding get() = _sliderBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _sliderBinding = FragmentSlideHandlerBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf<Fragment>(
            IntroSlide1Fragment(),
            IntroSlide2Fragment(),
            IntroSlide3Fragment(),
            IntroSlide4Fragment()
        )

        val sliderAdapter = SliderViewPagerAdapter(
            fragmentList,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )

        sliderBinding.sliderFragmentViewPager.adapter = sliderAdapter

        // attach view pager adapter to dots indicator view
        sliderBinding.dotsIndicator.attachTo(sliderBinding.sliderFragmentViewPager)

        return sliderBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _sliderBinding = null
    }
}
