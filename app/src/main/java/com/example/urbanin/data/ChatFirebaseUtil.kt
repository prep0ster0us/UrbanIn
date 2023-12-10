package com.example.urbanin.data

import android.net.Uri
import android.util.Log
import com.example.urbanin.MainActivity.Companion.TAG
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object ChatFirebaseUtil {

    fun getChatroomReference(chatroomID: String): DocumentReference {
        return FirebaseFirestore.getInstance()
            .collection("Chatrooms")
            .document(chatroomID)
    }

    fun getChatroomId(userID1: String, userID2: String): String {
        return if(userID1.hashCode() < userID2.hashCode()) {
            "${userID1}_${userID2}"
        } else {
            "${userID2}_${userID1}"
        }
    }

    fun getChatRoomMsgReference(chatroomID: String): CollectionReference {
        return getChatroomReference(chatroomID)
            .collection("chats")
    }
}