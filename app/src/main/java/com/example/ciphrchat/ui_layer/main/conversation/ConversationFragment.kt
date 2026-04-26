package com.example.ciphrchat.ui_layer.main.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.ui_layer.main.MainActivityViewModel

class ConversationFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: MessageAdapter
    private lateinit var peerUsername: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_conversation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        peerUsername = requireArguments().getString("peerUsername")!!

        adapter = MessageAdapter()

        val recyclerView = view.findViewById<RecyclerView>(R.id.conversationFragment_recyclerView_messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val editText = view.findViewById<EditText>(R.id.conversationFragment_editText_message)
        val sendButton = view.findViewById<Button>(R.id.conversationFragment_button_send)

        sendButton.setOnClickListener {
            val content = editText.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.sendMessage(peerUsername, content)
                editText.text.clear()
            }
        }

        viewModel.conversationManager.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages[peerUsername] ?: emptyList())
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }
}
