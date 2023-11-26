package com.example.urbanin.tenant.search.DetailedListing

import android.net.Uri
data class ListingMediaItem(
    val type: ItemType,
    val data: Uri
) {
    enum class ItemType {
        IMAGE,
        VIDEO
    }
}
