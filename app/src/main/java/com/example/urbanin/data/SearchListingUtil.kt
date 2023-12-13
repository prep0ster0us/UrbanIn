package com.example.urbanin.data

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.example.urbanin.MainActivity
import com.example.urbanin.R

object SearchListingUtil {

    fun setSearchBarVisibility(activity: FragmentActivity, flag: Boolean) {
        val parentSearchBar: View = activity.findViewById(R.id.listingSearchBarLayout)
        parentSearchBar.isVisible = flag
    }
    fun setTenantNavBarVisibility(activity: FragmentActivity, flag: Boolean) {
        val parentNavBar: View = activity.findViewById(R.id.bottom_navbar)
        parentNavBar.isVisible = flag
    }
    fun setLandlordNavBarVisibility(activity: FragmentActivity, flag: Boolean) {
        val parentNavBar: View = activity.findViewById(R.id.bottomNavigationView)
        parentNavBar.isVisible = flag
    }
    fun setRoommateNavBarVisibility(activity: FragmentActivity, flag: Boolean) {
        val parentNavBar: View = activity.findViewById(R.id.roommate_bottom_bar)
        parentNavBar.isVisible = flag
    }

}