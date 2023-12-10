package com.example.urbanin.data

import com.google.firebase.Timestamp

data class ChatroomModel(
    var chatroomID: String="",
    var userIds: ArrayList<String> = arrayListOf(),
    var lastMsgSenderId: String="",
    var lastMsgTimestamp: Timestamp= Timestamp.now()
)
