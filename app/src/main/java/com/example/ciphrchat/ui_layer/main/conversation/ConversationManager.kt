package com.example.ciphrchat.ui_layer.main.conversation

import androidx.lifecycle.MutableLiveData
import com.example.ciphrchat.data_layer.models.Message
import com.example.ciphrchat.data_layer.repositories.ContactRepository
import com.example.ciphrchat.data_layer.repositories.MessageRepository
import com.example.ciphrchat.data_layer.repositories.SessionRepository

class ConversationManager {
    val messages = MutableLiveData<Map<String, List<Message>>>(emptyMap())

    val outgoingMessages = mutableMapOf<Long, Message>()


    suspend fun load() {
        val loaded = mutableMapOf<String, List<Message>>()
        for (contact in ContactRepository.getContacts()) {
            loaded[contact.username] = MessageRepository.getMessagesByPeerUsername(contact.username)
        }
        messages.value = loaded
    }

    fun saveOutgoing(timestamp: Long, toUsername: String, content: String) {
        outgoingMessages[timestamp] = Message(
            content = content,
            senderUsername = SessionRepository.session.username,
            peerUsername = toUsername,
            sentAt = timestamp
        )
    }

    suspend fun flushOutgoing(timestamp: Long) {
        val msg = outgoingMessages[timestamp] ?: return
        MessageRepository.saveMessage(msg.content, msg.senderUsername, msg.peerUsername, msg.sentAt)
        load()
    }

    fun removeOutgoing(timestamp: Long) {
        outgoingMessages.remove(timestamp)
    }

    suspend fun saveIncoming(senderUsername: String, content: String) {
        MessageRepository.saveMessage(
            content = content,
            senderUsername = senderUsername,
            peerUsername = senderUsername,
            sentAt = System.currentTimeMillis()
        )
        load()
    }
}
