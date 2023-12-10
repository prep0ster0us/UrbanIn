package com.example.urbanin.data

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class ChatListAdapter(
    private val options: FirestoreRecyclerOptions<ChatMessageModel>,
    private val context: Context?
//) : RecyclerView.Adapter<ChatListAdapter.ChatMessageViewHolder>() {
) : FirestoreRecyclerAdapter<ChatMessageModel, ChatListAdapter.ChatMessageViewHolder>(options) {

    private lateinit var contextViewGroup: ViewGroup
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessageViewHolder {
        contextViewGroup = parent
        val chatItemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_layout, parent, false)

        return ChatMessageViewHolder(chatItemView)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int, model: ChatMessageModel) {
        with(holder) {
            if (model.senderID == ChatFirebaseUtil.getCurrentUserId()) {
                senderLayout.visibility = View.VISIBLE
                senderMsg.text = model.message
                senderTimestamp.text = ChatFirebaseUtil.formatTimestamp(model.timeStamp)

                receiverLayout.visibility = View.GONE
            } else {
                receiverLayout.visibility = View.VISIBLE
                receiverMsg.text = model.message
                receiverTimestamp.text = ChatFirebaseUtil.formatTimestamp(model.timeStamp)

                senderLayout.visibility = View.GONE
            }
        }
    }

    class ChatMessageViewHolder(chatItemView: View) : RecyclerView.ViewHolder(chatItemView) {
        val receiverLayout: RelativeLayout = chatItemView.findViewById(R.id.receiver_msg_layout)
        val receiverMsg: TextView = chatItemView.findViewById(R.id.receiverMsgText)
        val receiverTimestamp: TextView = chatItemView.findViewById(R.id.receiverMsgTimestamp)

        val senderLayout: RelativeLayout = chatItemView.findViewById(R.id.sender_msg_layout)
        val senderMsg: TextView = chatItemView.findViewById(R.id.senderMsgText)
        val senderTimestamp: TextView = chatItemView.findViewById(R.id.senderMsgTimestamp)
    }
}