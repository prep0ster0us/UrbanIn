package com.example.urbanin.data

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso

class MessageListAdapter(
    private val options: FirestoreRecyclerOptions<MessageDataModel>,
    private val context: Context,
    private val handler: Callbacks
) : FirestoreRecyclerAdapter<MessageDataModel, MessageListAdapter.ChatMessageViewHolder>(options) {

    private lateinit var contextViewGroup: ViewGroup
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessageViewHolder {
        contextViewGroup = parent
        val msgItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.message_card_layout, parent, false)

        return ChatMessageViewHolder(msgItemView)
    }

    override fun onBindViewHolder(
        holder: ChatMessageViewHolder,
        position: Int,
        model: MessageDataModel
    ) {

        with(holder) {
            Toast.makeText(context, model.address, Toast.LENGTH_SHORT).show()
            msgAddress.text = model.address
            Picasso.get().load(model.image).into(msgImage)
            msgRecentMessage.text = model.recentMsg
            msgTimestamp.text = ChatFirebaseUtil.formatTimestamp(model.recentTimestamp)

            itemView.setOnClickListener {
                Log.d(TAG, "Chatroom: ${model.chatroomId}")

                handler.handleMessageData(
                    ChatMessageData(
                        model.chatroomId,
                        model.userId,
                        model.address,
                        model.image
                    )
                )
            }
        }
    }

    class ChatMessageViewHolder(msgItemView: View) : RecyclerView.ViewHolder(msgItemView) {
        val msgAddress: TextView = msgItemView.findViewById(R.id.address)
        val msgImage: ImageView = msgItemView.findViewById(R.id.listingImage)
        val msgRecentMessage: TextView = msgItemView.findViewById(R.id.recentMessage)
        val msgTimestamp: TextView = msgItemView.findViewById(R.id.recentTimestamp)
    }

    interface Callbacks {
        fun handleMessageData(data: ChatMessageData) {

        }
    }
}