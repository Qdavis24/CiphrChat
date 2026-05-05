package com.example.ciphrchat.ui_layer.main.managers

import androidx.lifecycle.MutableLiveData
import com.example.ciphrchat.data_layer.models.Conversation
import com.example.ciphrchat.data_layer.models.Message
import com.example.ciphrchat.data_layer.repositories.ContactRepository
import com.example.ciphrchat.data_layer.repositories.MessageRepository
import com.example.ciphrchat.data_layer.repositories.SessionRepository


class ConversationsManager {
    val conversations = MutableLiveData<List<Conversation>>(emptyList())

    val outgoingMessages = hashMapOf<Long, Message>()

    var activeConversation: String? = null

    suspend fun load() {
        val current = arrayListOf<Conversation>()
        val contacts = ContactRepository.getContacts()
        contacts.forEach {
            current.add(
                Conversation(
                    contact = it,
                    MessageRepository.getMessagesByContactUsername(it.username)
                )
            )
        }
        conversations.value = current
    }

    fun cacheOutgoing(timestamp: Long, toUsername: String, content: String) {
        outgoingMessages[timestamp] = Message(
            content = content,
            senderUsername = SessionRepository.session.username,
            contactUsername = toUsername,
            sentAt = timestamp
        )
    }

    suspend fun flushOutgoing(timestamp: Long) {
        val msg = outgoingMessages[timestamp] ?: return
        MessageRepository.saveMessage(
            msg.content, msg.senderUsername, msg.contactUsername, msg.sentAt, true
        )
        outgoingMessages.remove(timestamp)
        load()
    }

    fun removeOutgoing(timestamp: Long) {
        outgoingMessages.remove(timestamp)
    }

    suspend fun saveIncoming(senderUsername: String, content: String) {
        MessageRepository.saveMessage(
            content = content,
            senderUsername = senderUsername,
            contactUsername = senderUsername,
            sentAt = System.currentTimeMillis(),
            read = activeConversation?.equals(senderUsername) ?: false
        )
        load()
    }

    suspend fun markMessagesRead(contactUsername: String){
        MessageRepository.markReadByContactUsername(contactUsername)
        load()
    }

}