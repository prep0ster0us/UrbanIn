package com.example.urbanin.tenant.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.data.ChatMessageData
import com.example.urbanin.data.LoginPreferenceManager
import com.example.urbanin.data.MessageDataModel
import com.example.urbanin.data.MessageListAdapter
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.databinding.FragmentMessageBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
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
            binding.loggedInView.visibility = View.VISIBLE
            binding.loggedOutView.visibility = View.GONE
            binding.emptyLayout.visibility = View.GONE

            setupMessageRecyclerView()
        } else {
            binding.loggedInView.visibility = View.GONE
            binding.loggedOutView.visibility = View.VISIBLE
            binding.emptyLayout.visibility = View.GONE

            binding.messageLoginBtn.setOnClickListener {
                prefManager.setRedirectContext("Message")
                findNavController().navigate(MessageFragmentDirections.navigateMessageToLogin())
            }
        }
    }

    private fun setupMessageRecyclerView() {
        val query = db.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("messages")
            .orderBy("recentTimestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<MessageDataModel>()
            .setQuery(query, MessageDataModel::class.java)
            .build()

        adapter = MessageListAdapter(options, requireContext(), this)

        binding.msgRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.msgRecyclerView.adapter = adapter
        adapter.startListening()

        binding.messageReadStatus.visibility = View.GONE
        binding.emptyLayout.visibility = View.VISIBLE

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.emptyLayout.visibility = View.GONE
                binding.messageReadStatus.visibility = View.VISIBLE
                binding.messageReadStatus.text = if(adapter.itemCount == 1) {
                     "1 message"
                } else {
                    "${adapter.itemCount} messages"
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        SearchListingUtil.setTenantNavBarVisibility(requireActivity(), true)
        return binding.root
    }

    override fun handleMessageData(data: ChatMessageData) {
        val action = MessageFragmentDirections.navigateMessageToChat(data)
        findNavController().navigate(action)
    }

}