package com.example.ciphrchat.data_layer.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ciphrchat.data_layer.models.Conversation
import com.example.ciphrchat.data_layer.models.Message
import com.example.ciphrchat.data_layer.models.User

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "local-storage.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE user (
                _id          INTEGER PRIMARY KEY AUTOINCREMENT,
                username     TEXT NOT NULL UNIQUE,
                pub_key      TEXT NOT NULL,
                private_key  TEXT NOT NULL
            )
        """.trimIndent()
        )

        db?.execSQL(
            """
            CREATE TABLE conversations (
                convo_id        INTEGER PRIMARY KEY AUTOINCREMENT,
                peer_username   TEXT NOT NULL UNIQUE,
                peer_pub_key    TEXT NOT NULL
            )
        """.trimIndent()
        )

        db?.execSQL(
            """
            CREATE TABLE messages (
                message_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                content         TEXT NOT NULL,
                sender_username TEXT NOT NULL,
                sent_at         INTEGER NOT NULL,
                conversation_id INTEGER NOT NULL REFERENCES conversations(convo_id)
            )
        """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS messages")
        db?.execSQL("DROP TABLE IF EXISTS conversations")
        db?.execSQL("DROP TABLE IF EXISTS user")
        onCreate(db)
    }

    // --- user ---

    suspend fun insertUser(username: String, pubKey: String, privateKey: String): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("username", username)
                put("pub_key", pubKey)
                put("private_key", privateKey)
            }
            writableDatabase.insert("user", null, cv)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }


    suspend fun getUser(): User? {
        val cursor = readableDatabase.query("user", null, null, null, null, null, null, "1")

        return cursor.use {
            if (it.moveToFirst()) {
                User(
                    username = it.getString(it.getColumnIndexOrThrow("username")),
                    publicKey = it.getString(it.getColumnIndexOrThrow("pub_key")),
                    privateKey = it.getString(it.getColumnIndexOrThrow("private_key"))
                )
            } else null
        }
    }

    // --- conversations ---

    suspend fun insertConversation(peerUsername: String, peerPubKey: String): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("peer_username", peerUsername)
                put("peer_pub_key", peerPubKey)
            }
            writableDatabase.insert("conversations", null, cv)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun getConversations(): List<Conversation> {
        val cursor = readableDatabase.query("conversations", null, null, null, null, null, null)
        return cursor.use {
            val results =  ArrayList<Conversation>()
            while (it.moveToNext()) {
                results.add(
                    Conversation(
                        convoId = it.getInt(it.getColumnIndexOrThrow("convo_id")),
                        peerUsername = it.getString(it.getColumnIndexOrThrow("peer_username")),
                        peerPubKey = it.getString(it.getColumnIndexOrThrow("peer_pub_key"))
                    )
                )
            }
            results
        }
    }

    // --- messages ---

    suspend fun insertMessage(
        conversationId: Int, content: String, senderUsername: String, sentAt: Long
    ): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("conversation_id", conversationId)
                put("content", content)
                put("sender_username", senderUsername)
                put("sent_at", sentAt)
            }
            writableDatabase.insert("messages", null, cv)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun getMessagesByConversationId(conversationId: Int): List<Message> {
        val cursor = readableDatabase.query(
            "messages",
            null,
            "conversation_id = ?",
            arrayOf(conversationId.toString()),
            null,
            null,
            "sent_at ASC"
        )
        return cursor.use {
            val results = ArrayList<Message>()
            while (it.moveToNext()) {
                results.add(
                    Message(
                        messageId = it.getInt(it.getColumnIndexOrThrow("message_id")),
                        conversationId = it.getInt(it.getColumnIndexOrThrow("conversation_id")),
                        content = it.getString(it.getColumnIndexOrThrow("content")),
                        senderUsername = it.getString(it.getColumnIndexOrThrow("sender_username")),
                        sentAt = it.getLong(it.getColumnIndexOrThrow("sent_at"))
                    )
                )
            }
            results
        }
    }
}