package com.example.ciphrchat.data_layer.repositories

import com.example.ciphrchat.data_layer.database.LocalDatabaseHelper
import com.example.ciphrchat.data_layer.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MessageRepository {
    private lateinit var db: LocalDatabaseHelper

    fun init(db: LocalDatabaseHelper) { this.db = db }

    suspend fun saveMessage(
        content: String,
        senderUsername: String,
        peerUsername: String,
        sentAt: Long
    ): Boolean = withContext(Dispatchers.IO) { db.insertMessage(content, senderUsername, peerUsername, sentAt) }

    suspend fun getMessagesByPeerUsername(peerUsername: String): List<Message> =
        withContext(Dispatchers.IO) { db.getMessagesByPeerUsername(peerUsername) }
}
