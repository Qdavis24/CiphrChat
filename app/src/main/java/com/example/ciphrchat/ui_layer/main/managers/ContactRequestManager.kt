package com.example.ciphrchat.ui_layer.main.managers

import androidx.lifecycle.MutableLiveData
import com.example.ciphrchat.data_layer.models.ContactRequest

class ContactRequestManager {
    val contactRequests = MutableLiveData<List<ContactRequest>>(emptyList())

    fun add(fromUsername: String, fromPubKey: String) {
        val current = contactRequests.value!!.toMutableList()
        current.add(ContactRequest(fromUsername = fromUsername, pubKey = fromPubKey))
        contactRequests.value = current
    }


    fun remove(request: ContactRequest) {
        val current = contactRequests.value!!.toMutableList()
        current.removeAll { it.fromUsername == request.fromUsername }
        contactRequests.value = current
    }

    fun get(fromUsername: String): ContactRequest? =
        contactRequests.value?.find { it.fromUsername == fromUsername }

}