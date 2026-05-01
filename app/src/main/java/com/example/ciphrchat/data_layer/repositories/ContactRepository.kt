package com.example.ciphrchat.data_layer.repositories

import com.example.ciphrchat.data_layer.database.LocalDatabaseHelper
import com.example.ciphrchat.data_layer.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactRepository {
    private lateinit var db: LocalDatabaseHelper

    fun init(db: LocalDatabaseHelper) {
        this.db = db
    }

    suspend fun saveContact(username: String, pubKey: String): Boolean =
        withContext(Dispatchers.IO) { db.insertContact(username, pubKey) }

    suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) { db.getContacts() }

    suspend fun getContactByUsername(username: String): Contact? =
        withContext(Dispatchers.IO) { db.getContactByUsername(username) }
}
