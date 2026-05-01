package com.example.ciphrchat.ui_layer.main.contact_request

import androidx.lifecycle.MutableLiveData
import com.example.ciphrchat.data_layer.models.ContactRequest

class ContactRequestManager {
    val contactRequests = MutableLiveData<List<ContactRequest>>(emptyList())
    val unreadCount = MutableLiveData<Int>(0)

    fun add(fromUsername: String, fromPubKey: String) {
        val current = contactRequests.value!!.toMutableList()
        current.add(ContactRequest(fromUsername = fromUsername, pubKey = fromPubKey))
        contactRequests.value = current
        unreadCount.value = unreadCount.value!! + 1
    }


    fun remove(request: ContactRequest) {
        val current = contactRequests.value!!.toMutableList()
        current.removeAll { it.fromUsername == request.fromUsername }
        contactRequests.value = current
    }

    fun get(fromUsername: String): ContactRequest? =
        contactRequests.value?.find { it.fromUsername == fromUsername }

    fun markAllRead() {
        unreadCount.value = 0
    }
}
