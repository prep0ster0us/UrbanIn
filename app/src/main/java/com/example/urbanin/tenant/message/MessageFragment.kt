package com.example.urbanin.tenant.message

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R
import com.example.urbanin.data.ChatFirebaseUtil
import com.example.urbanin.data.ChatListAdapter
import com.example.urbanin.data.ChatMessageData
import com.example.urbanin.data.ChatroomModel
import com.example.urbanin.data.ListingData
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.MessageDataModel
import com.example.urbanin.data.MessageListAdapter
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.FragmentAccountBinding
import com.example.urbanin.databinding.FragmentMessageBinding
import com.example.urbanin.tenant.search.SearchListViewFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageFragment : Fragment(), MessageListAdapter.Callbacks {

    private lateinit var binding: FragmentMessageBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var prefManager: LoginPreferenceManager
    private lateinit var adapter: MessageListAdapter

    private lateinit var messageColRef: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentMessageBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        prefManager = LoginPreferenceManager(requireContext())

        if (prefManager.isLoggedIn()) {
            toggleLoggedInView(true)

            setupMessageRecyclerView()
        } else {
            toggleLoggedInView(false)
            binding.messageLoginBtn.setOnClickListener {
                findNavController().navigate(MessageFragmentDirections.navigateMessageToLogin())
            }
        }

        SearchListingUtil.setTenantNavBarVisibility(requireActivity(), true)
    }

    private fun setupMessageRecyclerView() {
        val query = db.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("messages")
            .orderBy("recentTimestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<MessageDataModel>()
            .setQuery(query, MessageDataModel::class.java)
            .build()

        Toast.makeText(requireContext(), auth.currentUser!!.uid.toString(), Toast.LENGTH_SHORT).show()

        adapter = MessageListAdapter(options, requireContext(), this)

        binding.msgRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.msgRecyclerView.adapter = adapter
        adapter.startListening()

//        // scroll to most recent message
//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                super.onItemRangeInserted(positionStart, itemCount)
//                binding.msgRecyclerView.smoothScrollToPosition(0)
//            }
//        })
    }

    private fun toggleLoggedInView(flag: Boolean) {
        binding.loggedInView.isVisible = flag
        binding.loggedOutView.isVisible = !flag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun handleMessageData(data: ChatMessageData) {
        val action = MessageFragmentDirections.navigateMessageToChat(data)
        findNavController().navigate(action)
    }

}