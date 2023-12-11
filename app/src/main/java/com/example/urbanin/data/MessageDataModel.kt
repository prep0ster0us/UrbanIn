package com.example.urbanin.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

data class MessageDataModel(
    var chatroomId: String="",
    var address: String="",
    var image: String="",
    val userId: String="",
    var recentMsg: String="",
    val recentTimestamp: Timestamp=Timestamp.now()
)
