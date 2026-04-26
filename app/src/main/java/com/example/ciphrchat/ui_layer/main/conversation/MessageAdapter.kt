package com.example.ciphrchat.ui_layer.main.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private var messages = listOf<Message>()

    fun submitList(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvInitials: TextView = view.findViewById(R.id.messageItem_textView_initials)
        private val tvUsername: TextView = view.findViewById(R.id.messageItem_textView_username)
        private val tvContent: TextView = view.findViewById(R.id.messageItem_textView_content)
        private val tvTimestamp: TextView = view.findViewById(R.id.messageItem_textView_timestamp)

        fun update(message: Message) {
            tvInitials.text = message.senderUsername.first().uppercaseChar().toString()
            tvUsername.text = message.senderUsername
            tvContent.text = message.content
            tvTimestamp.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.sentAt))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(messages[position])
    }

    override fun getItemCount() = messages.size
}
