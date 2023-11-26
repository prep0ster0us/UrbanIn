package com.example.urbanin.landlord.DetailedListing

import android.net.Uri
data class LandlordListingMediaItem(
    val type: ItemType,
    val data: Uri
) {
    enum class ItemType {
        IMAGE,
        VIDEO
    }
}
