package com.example.urbanin.roommates.message

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.data.ChatFirebaseUtil
import com.example.urbanin.data.ChatListAdapter
import com.example.urbanin.data.ChatMessageModel
import com.example.urbanin.data.ChatroomModel
import com.example.urbanin.data.MessageDataModel
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.RoommateFragmentChatBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class RoommateChatFragment : Fragment() {
    private lateinit var binding: RoommateFragmentChatBinding

    // receive userId
    private val args: RoommateChatFragmentArgs by navArgs<RoommateChatFragmentArgs>()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var chatroomId: String
    private lateinit var chatroomModel: ChatroomModel
    private lateinit var messageModel: MessageDataModel

    private lateinit var adapter: ChatListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RoommateFragmentChatBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        fetchReceiverUserInfo()

        senderId = auth.currentUser!!.uid
        receiverId = args.rmChatData.landlordId

        if(senderId != receiverId) {
            chatroomId = ChatFirebaseUtil.getChatroomId(senderId, receiverId)

            db.collection("RoommateChatrooms")
                .document(chatroomId)
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
                                db.collection("RoommateChatrooms")
                                    .document(chatroomId)
                                    .set(chatroomModel)
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
        }

        binding.btnChatSend.setOnClickListener {
            val message = binding.chatMessageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToUser(message)
            }
        }
        SearchListingUtil.setRoommateNavBarVisibility(requireActivity(), false)
    }


    private fun fetchReceiverUserInfo() {
        db.collection("Users")
            .document(args.rmChatData.landlordId)
            .get()
            .addOnSuccessListener { doc ->
                Log.d(TAG, "getReceiverUserInfo: SUCCESS")
                // set user name in chat window
                binding.userName.text = "${doc.data!!["First Name"]} ${doc.data!!["Last Name"]}"
                // set profile image, if any
                val photoUrl = doc.data!!["profileImage"] as String
                if (photoUrl.isNotEmpty()) {
                    Picasso.get().load(Uri.parse(photoUrl)).into(binding.userProfileImage)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "getReceiverUserInfo: FAILED- $exception")
            }
    }

    private fun setupChatRecyclerView() {
        val query = db.collection("RoommateChatrooms")
            .document(chatroomId)
            .collection("chats")
            .orderBy("timeStamp", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .build()

        adapter = ChatListAdapter(options, requireContext())

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter
        adapter.startListening()

        // scroll to most recent message
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
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
        db.collection("RoommateChatrooms")
            .document(chatroomId)
            .collection("chats")
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
        db.collection("RoommateChatrooms")
            .document(chatroomId)
            .set(chatroomModel)

        // save/update chatroom in user's message collection
        saveChatroomToUser(senderId, receiverId, message, Timestamp.now())
        saveChatroomToUser(receiverId, senderId, message, Timestamp.now())

    }

    private fun saveChatroomToUser(
        sendId: String,
        receiveId: String,
        msg: String,
        timeStamp: Timestamp
    ) {
        // save for sender
        db.collection("Users")
            .document(sendId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.data != null) {
                    // fetch listing landlord name, and save to user's messages collection
                    db.collection("Users")
                        .document(sendId)
                        .collection("RoommateMessages")
                        .document(receiveId)
                        .set(
                            MessageDataModel(
                                chatroomId,
                                args.rmChatData.listingAddress,
                                args.rmChatData.listingImageUrl,
                                receiveId,
                                sendId,
                                msg,
                                timeStamp
                            )
                        )
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "couldn't save chatroom to user: $sendId- $exception")
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        with(binding) {
            btnBackToMessage.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }
}