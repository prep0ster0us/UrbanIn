package com.example.urbanin.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ChatMessageModel(
    var message: String="",
    var senderID: String="",
    @ServerTimestamp var timeStamp: Timestamp= Timestamp.now()
)
