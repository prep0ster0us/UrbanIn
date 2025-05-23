package com.example.urbanin.data

import android.util.Log
import android.widget.Toast
import com.example.urbanin.MainActivity.Companion.TAG
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun formatTimestamp(timestamp: Timestamp): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp.seconds * 1000
        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val minutes = cal.get(Calendar.MINUTE)

        val dateFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
        return dateFormat.format(Date(timestamp.seconds * 1000))
    }

    fun getReceiverChatrooms(receiverId: String): Query {
        return FirebaseFirestore.getInstance()
            .collection("Chatrooms")
            .whereArrayContains("userIds", receiverId)
    }

    fun checkIfSentLast(userId: String): Boolean {
        Log.w(TAG, "$userId is not ${FirebaseAuth.getInstance().currentUser!!.uid}")
        return userId == FirebaseAuth.getInstance().currentUser!!.uid
    }

}