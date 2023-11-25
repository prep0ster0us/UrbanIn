package com.example.urbanin.landlord.AddListing

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class MediaPager(
    private var views: List<View>,
    private var context: Context
): PagerAdapter() {

    override fun getCount(): Int {
        return views.size
    }

    override fun getItemPosition(`object`: Any): Int {
        for (index in views.indices) {
            if (`object` == views[index]) {
                return index
            }
        }
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(views[position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position]
        container.addView(view)
        return view
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "View ${position + 1}"
    }

}
