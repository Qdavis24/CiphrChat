package com.example.ciphrchat.ui_layer.main.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.ui_layer.main.MainActivityViewModel
import kotlinx.coroutines.launch

class ConversationFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: MessageAdapter
    private lateinit var peerUsername: String

    private lateinit var editTextMessage: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_conversation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        peerUsername = requireArguments().getString("peerUsername")!!

        adapter = MessageAdapter()

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.conversationFragment_recyclerView_messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        editTextMessage = view.findViewById<EditText>(R.id.conversationFragment_editText_message)
        view.findViewById<Button>(R.id.conversationFragment_button_send)
            .setOnClickListener { sendMessage() }

        viewModel.conversationsManager.conversations.observe(viewLifecycleOwner) { conversations ->
            adapter.submitList(
                conversations.find { it.contact.username == peerUsername }?.messages ?: emptyList()
            )
            if (adapter.itemCount > 0){
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        val content = editTextMessage.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a message!", Toast.LENGTH_SHORT).show()
            return
        }
        if (content.length >= 245) { // RSA limit
            Toast.makeText(
                requireContext(),
                "Message exceeds length limit of 244 characters!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.sendMessage(peerUsername, content)
        editTextMessage.text.clear()

    }

    override fun onResume() {
        super.onResume()
        viewModel.conversationsManager.activeConversation = peerUsername
        lifecycleScope.launch {
            viewModel.conversationsManager.markMessagesRead(peerUsername)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.conversationsManager.activeConversation = null
    }
}
