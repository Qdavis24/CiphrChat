package com.example.ciphrchat.data_layer.repositories

import com.example.ciphrchat.data_layer.database.LocalDatabaseHelper
import com.example.ciphrchat.data_layer.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UserRepository {
    private lateinit var db: LocalDatabaseHelper

    fun init(db: LocalDatabaseHelper) { this.db = db }

    suspend fun saveUser(username: String, pubKey: String, privateKey: String): Boolean =
        withContext(Dispatchers.IO) { db.insertUser(username, pubKey, privateKey) }

    suspend fun getUser(): User? =
        withContext(Dispatchers.IO) { db.getUser() }
}
