package com.example.urbanin.data

import com.google.firebase.Timestamp

data class ChatMessageModel(
    var message: String="",
    var senderID: String="",
    var timeStamp: Timestamp= Timestamp.now()
)

//class ChatMessageModel {
//    private lateinit var message: String
//    private lateinit var senderID: String
//    private lateinit var timeStamp: Timestamp
//
//    constructor()
//    constructor(message: String, senderID: String, timeStamp: Timestamp) {
//        this.message = message
//        this.senderID = senderID
//        this.timeStamp = timeStamp
//    }
//
//}
