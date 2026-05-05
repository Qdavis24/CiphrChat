package com.example.ciphrchat.ui_layer.main.managers

import com.example.ciphrchat.data_layer.repositories.ContactRepository

class ContactsManager {
    // pretty bare but if we ever did anything more with contacts it would go here
    suspend fun addContact(username: String, pubKey: String) {
        ContactRepository.saveContact(username, pubKey)
    }
}