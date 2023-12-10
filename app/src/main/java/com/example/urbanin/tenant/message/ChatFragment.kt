package com.example.urbanin.tenant.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.data.ChatFirebaseUtil
import com.example.urbanin.data.ChatListAdapter
import com.example.urbanin.data.ChatMessageModel
import com.example.urbanin.data.ChatroomModel
import com.example.urbanin.databinding.FragmentChatBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding

    // receive userId
    private val args: ChatFragmentArgs by navArgs<ChatFragmentArgs>()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var chatroomId: String
    private lateinit var chatroomModel: ChatroomModel

    private lateinit var adapter: ChatListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentChatBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // TODO: fetch profile image of receiver
        // TODO: NOTE- need to add profile image url to "Users" collection in database
//        db.collection("Users")
//            .document(args.receivedUserId)
//            .get()
//            .addOnSuccessListener { doc ->
//                Log.d(TAG, "getReceiverProfileImage: SUCCESS")
//                val photoUrl = doc.data!!["profileImage"] as String
//                if(photoUrl.isNotEmpty()) {
//                    Picasso.get().load(Uri.parse(photoUrl)).into(binding.userProfileImage)
//                }
//            }
//            .addOnFailureListener {exception ->
//                Log.w(TAG, "getReceiverProfileImage: FAILED- $exception")
//            }

        // TODO: get user ID of user who posted the listing
        senderId = auth.currentUser!!.uid
        receiverId = args.receivedUserId
        chatroomId = ChatFirebaseUtil.getChatroomId(senderId, receiverId)

        ChatFirebaseUtil.getChatroomReference(chatroomId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    with(task.result) {
                        if (data == null) {
                            chatroomModel = ChatroomModel(
                                chatroomId,
                                arrayListOf(senderId, receiverId),
                                "",
                                Timestamp.now()
                            )
                            // add new chatroom to database
                            ChatFirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                        } else {
                            chatroomModel = ChatroomModel(
                                data!!["chatroomID"] as String,
                                data!!["userIds"] as ArrayList<String>,
                                data!!["lastMsgSenderId"] as String,
                                data!!["lastMsgTimestamp"] as Timestamp
                            )
                        }
                    }

                }
            }

        setupChatRecyclerView()

        binding.btnChatSend.setOnClickListener {
            val message = binding.chatMessageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToUser(message)
            }
        }
    }

    private fun setupChatRecyclerView() {
        val query = ChatFirebaseUtil.getChatRoomMsgReference(chatroomId)
            .orderBy("timeStamp", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .build()

        adapter = ChatListAdapter(options, requireContext())

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter
        adapter.startListening()

        // scroll to most recent message
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.chatRecyclerView.smoothScrollToPosition(0)
            }
        })

    }

    private fun sendMessageToUser(message: String) {
        // create chat message document
        val chatMsg = ChatMessageModel(
            message,
            auth.currentUser!!.uid,
            Timestamp.now()
        )
        // add to database (within chatroom for these users, as a sub-collection)
        ChatFirebaseUtil.getChatRoomMsgReference(chatroomId)
            .add(chatMsg)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "send message: SUCCESS")
                    binding.chatMessageInput.text.clear()
                }
            }

        // update chatroom (between these users)
        chatroomModel.lastMsgTimestamp = Timestamp.now()
        chatroomModel.lastMsgSenderId = auth.currentUser!!.uid
        // update in database
        ChatFirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}