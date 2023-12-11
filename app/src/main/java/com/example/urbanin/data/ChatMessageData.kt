package com.example.urbanin.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessageData(
    var chatroomId: String="",
    var landlordId: String="",
    var listingAddress: String="",
    var listingImageUrl: String=""
) : Parcelable
