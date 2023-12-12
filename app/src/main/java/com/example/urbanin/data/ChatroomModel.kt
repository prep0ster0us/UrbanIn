package com.example.urbanin.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ChatroomModel(
    var chatroomID: String="",
    var userIds: ArrayList<String> = arrayListOf(),
    var lastMsgSenderId: String="",
    @ServerTimestamp var lastMsgTimestamp: Timestamp= Timestamp.now()
)
