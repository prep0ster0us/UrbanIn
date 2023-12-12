package com.example.urbanin.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LandlordChatMessageData(
    var chatroomId: String="",
    var receiverId: String="",
    var listingAddress: String="",
    var listingImageUrl: String=""
) : Parcelable
