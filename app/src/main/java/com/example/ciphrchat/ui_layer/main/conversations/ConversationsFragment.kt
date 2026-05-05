package com.example.ciphrchat.ui_layer.main.conversations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.Conversation
import com.example.ciphrchat.ui_layer.main.MainActivityViewModel

class ConversationsFragment : Fragment(), ConversationAdapter.ConversationListener {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ConversationAdapter(this)

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.fragmentConversations_recyclerView_conversations)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.conversationsManager.conversations.observe(viewLifecycleOwner) { conversations ->
            adapter.submitList(conversations)
        }
    }

    override fun onConversationClicked(conversation: Conversation) {
        findNavController().navigate(
            R.id.action_conversationsFragment_to_conversationFragment,
            Bundle().apply { putString("peerUsername", conversation.contact.username) })
    }
}