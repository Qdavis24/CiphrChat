package com.example.ciphrchat.data_layer.repositories

import com.example.ciphrchat.data_layer.database.LocalDatabaseHelper
import com.example.ciphrchat.data_layer.models.Conversation
import com.example.ciphrchat.data_layer.models.Message

object ConversationRepository {
    private lateinit var db: LocalDatabaseHelper

    fun init(db: LocalDatabaseHelper) {
        this.db = db
    }

    suspend fun createConversation(peerUsername: String, peerPubKey: String): Boolean {
        return db.insertConversation(peerUsername, peerPubKey)
    }

    suspend fun getConversations(): List<Conversation> {
        return db.getConversations()
    }

    suspend fun saveMessage(conversationId: Int, content: String, senderUsername: String, sentAt: Long): Boolean {
        return db.insertMessage(conversationId, content, senderUsername, sentAt)
    }

    suspend fun getMessages(conversationId: Int): List<Message> {
        return db.getMessagesByConversationId(conversationId)
    }
}