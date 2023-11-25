package com.example.urbanin.landlord.AddListing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.viewpager.widget.PagerAdapter
import com.example.urbanin.R

class MediaPagerAdapter (
    private val items: List<MediaPagerItem>,
    private val context: Context
) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val item = items[position]

        return when(item.type) {
            MediaPagerItem.ItemType.IMAGE -> {
                // inflate image layout
                val view = inflater.inflate(R.layout.add_listing_media_image_layout, container, false) as ImageView
                // set image resource (using Uri path of image (clicked by camera intent/selected from storage)
                view.setImageURI(item.data)
                // add to view pager container
                container.addView(view)
                view
            }

            MediaPagerItem.ItemType.VIDEO -> {
                val view = inflater.inflate(R.layout.add_listing_media_video_layout, container, false) as VideoView
                view.setVideoURI(item.data)
                // add playback controls (optional)
                /*
                    val mediaController = MediaController(context)
                    view.setMediaController((mediaController))
                    mediaController.setAnchorView(view)
                 */

                // seamlessly loop the video
                view.setOnPreparedListener { mp ->
                    mp.isLooping=true
                    mp.start()
                }
                container.addView(view)
                view
            }
        }
    }

    override fun getCount(): Int = items.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
