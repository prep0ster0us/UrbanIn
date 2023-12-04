package com.example.urbanin.data

import android.net.Uri
data class MediaItem(
    val type: ItemType,
    val data: Uri
) {
    enum class ItemType {
        IMAGE,
        VIDEO
    }
}
