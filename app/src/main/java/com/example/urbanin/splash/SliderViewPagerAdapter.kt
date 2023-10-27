package com.example.urbanin.splash

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class SliderViewPagerAdapter(list: ArrayList<Fragment>, fragManager: FragmentManager, lifeCycle: Lifecycle): FragmentStateAdapter(fragManager, lifeCycle) {
    private val fragmentList = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}