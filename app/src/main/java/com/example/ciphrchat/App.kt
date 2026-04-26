package com.example.ciphrchat

import android.app.Application
import com.example.ciphrchat.data_layer.database.LocalDatabaseHelper
import com.example.ciphrchat.data_layer.repositories.ContactRepository
import com.example.ciphrchat.data_layer.repositories.MessageRepository
import com.example.ciphrchat.data_layer.repositories.UserRepository

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val db = LocalDatabaseHelper(this)
        UserRepository.init(db)
        ContactRepository.init(db)
        MessageRepository.init(db)
    }
}