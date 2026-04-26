package com.example.ciphrchat.data_layer.repositories

import com.example.ciphrchat.data_layer.database.LocalDatabaseHelper
import com.example.ciphrchat.data_layer.models.User

object UserRepository {
    private lateinit var db: LocalDatabaseHelper

    fun init(db: LocalDatabaseHelper) {
        this.db = db
    }

    suspend fun saveUser(username: String, pubKey: String, privateKey: String): Boolean {
        return db.insertUser(username, pubKey, privateKey)
    }

    suspend fun getUser(): User? {
        return db.getUser()
    }
}