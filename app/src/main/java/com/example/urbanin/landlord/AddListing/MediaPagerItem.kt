package com.example.urbanin.landlord.AddListing

import android.net.Uri
data class MediaPagerItem(
    val type: ItemType,
    val data: Uri
) {
    enum class ItemType {
        IMAGE,
        VIDEO
    }
}
