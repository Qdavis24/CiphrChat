package com.example.ciphrchat.ui_layer.main.contacts

import androidx.lifecycle.MutableLiveData
import com.example.ciphrchat.data_layer.models.Contact
import com.example.ciphrchat.data_layer.repositories.ContactRepository

class ContactsManager {
    val contacts = MutableLiveData<List<Contact>>(emptyList())

    suspend fun load() {
        contacts.value = ContactRepository.getContacts()
    }

    suspend fun addContact(username: String, pubKey: String) {
        ContactRepository.saveContact(username, pubKey)
        load()
    }
}
