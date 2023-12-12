package com.example.urbanin.landlord.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.urbanin.R
import com.example.urbanin.data.ChatMessageData
import com.example.urbanin.data.LandlordChatMessageData
import com.example.urbanin.data.LandlordMessageListAdapter
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.MessageDataModel
import com.example.urbanin.data.MessageListAdapter
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.LandlordFragmentMessageBinding
import com.example.urbanin.tenant.message.MessageFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LandlordMessageFragment : Fragment(), LandlordMessageListAdapter.Callbacks  {

    private lateinit var binding: LandlordFragmentMessageBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var prefManager: LoginPreferenceManager
    private lateinit var adapter: LandlordMessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LandlordFragmentMessageBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        prefManager = LoginPreferenceManager(requireContext())

        setupMessageRecyclerView()

    }
    private fun setupMessageRecyclerView() {
        val query = db.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("messages")
//            .whereEqualTo("receiverId", auth.currentUser!!.uid)
            .orderBy("recentTimestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<MessageDataModel>()
            .setQuery(query, MessageDataModel::class.java)
            .build()

        adapter = LandlordMessageListAdapter(options, requireContext(), this)

        binding.landlordMsgRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.landlordMsgRecyclerView.adapter = adapter
        adapter.startListening()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SearchListingUtil.setLandlordNavBarVisibility(requireActivity(), true)
        return binding.root
    }

    override fun handleMessageData(data: LandlordChatMessageData) {
        val action = LandlordMessageFragmentDirections.navigateMessageToChat(data)
        findNavController().navigate(action)
    }

}