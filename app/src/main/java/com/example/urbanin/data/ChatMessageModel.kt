package com.example.urbanin.data

import com.google.firebase.Timestamp

data class ChatMessageModel(
    var message: String="",
    var senderID: String="",
    var timeStamp: Timestamp= Timestamp.now()
)
