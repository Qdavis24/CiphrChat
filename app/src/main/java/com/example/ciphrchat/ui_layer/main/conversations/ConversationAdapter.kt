package com.example.ciphrchat.ui_layer.main.conversations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.Conversation

class ConversationAdapter(val listener: ConversationListener) :
    RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    interface ConversationListener {
        fun onConversationClicked(conversation: Conversation)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUsername: TextView = view.findViewById(R.id.conversationItem_textView_username)
        private val tvInitials: TextView = view.findViewById(R.id.conversationItem_textView_initials)
        private val tvUnreadCount: TextView = view.findViewById(R.id.conversationItem_textView_unreadCount)

        fun update(convo: Conversation) {
            tvUsername.text = convo.contact.username
            tvInitials.text = convo.contact.username.first().uppercaseChar().toString()

            val unread = convo.messages.count { !it.read }
            if (unread > 0) {
                tvUnreadCount.visibility = View.VISIBLE
                tvUnreadCount.text = unread.toString()
            } else {
                tvUnreadCount.visibility = View.GONE
            }

            itemView.setOnClickListener { listener.onConversationClicked(convo) }
        }
    }

    private var conversations = listOf<Conversation>()

    fun submitList(newConversations: List<Conversation>) {
        conversations = newConversations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(conversations[position])
    }

    override fun getItemCount() = conversations.size

}