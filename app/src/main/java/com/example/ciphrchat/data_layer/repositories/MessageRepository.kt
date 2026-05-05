package com.example.ciphrchat.data_layer.repositories

import com.example.ciphrchat.data_layer.database.LocalDatabaseHelper
import com.example.ciphrchat.data_layer.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MessageRepository {
    private lateinit var db: LocalDatabaseHelper

    fun init(db: LocalDatabaseHelper) {
        this.db = db
    }

    suspend fun saveMessage(
        content: String, senderUsername: String, contactUsername: String, sentAt: Long, read: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        db.insertMessage(
            content, senderUsername, contactUsername, sentAt, read
        )
    }

    suspend fun getMessagesByContactUsername(contactUsername: String): ArrayList<Message> =
        withContext(Dispatchers.IO) { db.getMessagesByContactUsername(contactUsername) }

    suspend fun markReadByContactUsername(contactUsername: String) {
        withContext(Dispatchers.IO) { db.markMessagesReadByContactUsername(contactUsername) }
    }
}
