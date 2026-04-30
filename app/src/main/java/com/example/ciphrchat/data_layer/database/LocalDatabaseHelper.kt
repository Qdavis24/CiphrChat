package com.example.ciphrchat.data_layer.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.ciphrchat.data_layer.models.Contact
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
                username TEXT PRIMARY KEY,
                pub_key TEXT NOT NULL,
                private_key TEXT NOT NULL
            )
        """.trimIndent()
        )

        db?.execSQL(
            """
            CREATE TABLE contact (
                username TEXT PRIMARY KEY,
                pub_key TEXT NOT NULL
            )
        """.trimIndent()
        )

        db?.execSQL(
            """
            CREATE TABLE message (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                content TEXT NOT NULL,
                sender_username TEXT NOT NULL,
                sent_at INTEGER NOT NULL,
                contact_username TEXT NOT NULL REFERENCES contact(username)
            )
        """.trimIndent()
        )

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS message")
        db?.execSQL("DROP TABLE IF EXISTS contact")
        db?.execSQL("DROP TABLE IF EXISTS user")
        onCreate(db)
    }

    // --- user ---

    fun insertUser(username: String, pubKey: String, privateKey: String): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("username", username)
                put("pub_key", pubKey)
                put("private_key", privateKey)
            }
            writableDatabase.insert("user", null, cv)
            true
        } catch (e: SQLiteConstraintException) {
            Log.d("ERROR", "LocalDatabaseHelper::insertUser ${e.message}")
            false
        }
    }

    fun getUser(): User? {
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

    // --- contacts ---

    fun insertContact(username: String, pubKey: String): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("username", username)
                put("pub_key", pubKey)
            }
            writableDatabase.insert("contact", null, cv)
            true
        } catch (e: SQLiteConstraintException) {
            Log.d("ERROR", "LocalDatabaseHelper::insertContact ${e.message}")
            false
        }
    }

    fun getContacts(): List<Contact> {
        val cursor = readableDatabase.query("contact", null, null, null, null, null, null)
        return cursor.use {
            val results = ArrayList<Contact>()
            while (it.moveToNext()) {
                results.add(
                    Contact(
                        username = it.getString(it.getColumnIndexOrThrow("username")),
                        pubKey = it.getString(it.getColumnIndexOrThrow("pub_key"))
                    )
                )
            }
            results
        }
    }

    fun getContactByUsername(username: String): Contact? {
        val cursor = readableDatabase.query(
            "contact", null,
            "username = ?", arrayOf(username),
            null, null, null, "1"
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Contact(
                    username = it.getString(it.getColumnIndexOrThrow("username")),
                    pubKey = it.getString(it.getColumnIndexOrThrow("pub_key"))
                )
            } else null
        }
    }

    // --- messages ---

    fun insertMessage(
        content: String,
        senderUsername: String,
        contactUsername: String,
        sentAt: Long
    ): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("content", content)
                put("sender_username", senderUsername)
                put("contact_username", contactUsername)
                put("sent_at", sentAt)
            }
            writableDatabase.insert("message", null, cv)
            true
        } catch (e: SQLiteConstraintException) {
            Log.d("ERROR", "LocalDatabaseHelper::insertMessage ${e.message}")
            false
        }
    }

    fun getMessagesByContactUsername(contactUsername: String): List<Message> {
        val cursor = readableDatabase.query(
            "message", null,
            "contact_username = ?", arrayOf(contactUsername),
            null, null, "sent_at ASC"
        )
        return cursor.use {
            val results = ArrayList<Message>()
            while (it.moveToNext()) {
                results.add(
                    Message(
                        content = it.getString(it.getColumnIndexOrThrow("content")),
                        senderUsername = it.getString(it.getColumnIndexOrThrow("sender_username")),
                        contactUsername = it.getString(it.getColumnIndexOrThrow("contact_username")),
                        sentAt = it.getLong(it.getColumnIndexOrThrow("sent_at"))
                    )
                )
            }
            results
        }
    }
}