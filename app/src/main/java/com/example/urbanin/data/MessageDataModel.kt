package com.example.urbanin.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class MessageDataModel(
    var chatroomId: String="",
    var address: String="",
    var image: String="",
    val senderId: String="",
    val receiverId: String="",
    var recentMsg: String="",
    @ServerTimestamp val recentTimestamp: Timestamp=Timestamp.now()
)
