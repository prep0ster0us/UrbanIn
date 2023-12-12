package com.example.urbanin.data

import com.google.firebase.Timestamp

data class MessageDataModel(
    var chatroomId: String="",
    var address: String="",
    var image: String="",
    val senderId: String="",
    val receiverId: String="",
    var recentMsg: String="",
    val recentTimestamp: Timestamp=Timestamp.now()
)
