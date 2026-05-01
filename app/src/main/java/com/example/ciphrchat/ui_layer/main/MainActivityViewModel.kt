package com.example.ciphrchat.ui_layer.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ciphrchat.data_layer.models.OnlineUser
import com.example.ciphrchat.data_layer.repositories.ContactRepository
import com.example.ciphrchat.data_layer.repositories.SessionRepository
import com.example.ciphrchat.services.AuthApiService
import com.example.ciphrchat.services.SocketService
import com.example.ciphrchat.ui_layer.main.online_users.OnlineUsersManager
import com.example.ciphrchat.ui_layer.main.contact_request.ContactRequestManager
import com.example.ciphrchat.ui_layer.main.contacts.ContactsManager
import com.example.ciphrchat.ui_layer.main.conversation.ConversationManager
import com.example.ciphrchat.utils.CryptoUtils
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel(), SocketService.SocketListener {

    val socketService = SocketService(this)

    val onlineUsersManager = OnlineUsersManager()
    val contactRequestManager = ContactRequestManager()
    val contactsManager = ContactsManager()
    val conversationManager = ConversationManager()

    val isConnected = MutableLiveData<Boolean>(false)
    val error = MutableLiveData<String?>()

    init {
        viewModelScope.launch {
            contactsManager.load()
            conversationManager.load()
            socketService.connect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketService.disconnect()
    }

    fun onContactRequestsViewed() {
        contactRequestManager.markAllRead()
    }

    // --- emissions ---

    fun sendContactRequest(toUsername: String) {
        viewModelScope.launch {
            if (ContactRepository.getContactByUsername(toUsername) != null) error.postValue("$toUsername is already added as a contact")
            else socketService.sendContactRequest(toUsername, SessionRepository.session.publicKey)
        }
    }

    fun sendContactAccept(toUsername: String) {
        socketService.sendContactAccept(toUsername, SessionRepository.session.publicKey)
    }

    fun sendMessage(toUsername: String, content: String) {
        viewModelScope.launch {
            val contact = ContactRepository.getContactByUsername(toUsername) ?: return@launch
            val timestamp = System.currentTimeMillis()
            conversationManager.saveOutgoing(timestamp, toUsername, content)
            val encrypted = CryptoUtils.encrypt(content, contact.pubKey)
            socketService.sendMessage(timestamp, toUsername, encrypted)
        }
    }

    // --- broadcasted callbacks ---

    override fun onConnected() {
        val myUsername = SessionRepository.session.username
        viewModelScope.launch {
            val users = AuthApiService.getOnlineUsers()
            val filtered = users.filter { it != myUsername }.map {
                OnlineUser(
                    username = it
                )
            }
            onlineUsersManager.set(filtered)
            isConnected.value = true
            socketService.announceOnline()
        }
    }

    override fun onUserOnline(username: String) {
        onlineUsersManager.add(username)
    }

    override fun onUserOffline(username: String) {
        onlineUsersManager.remove(username)
    }

    // --- targeted callbacks ---
    override fun onMessageReceived(fromUsername: String, content: String) {
        viewModelScope.launch {
            val isContact = ContactRepository.getContactByUsername(fromUsername) != null
            if (!isContact) return@launch
            val cleartext = CryptoUtils.decrypt(content, SessionRepository.session.privateKey)
            conversationManager.saveIncoming(fromUsername, cleartext)
        }
    }

    override fun onContactRequestReceived(fromUsername: String, fromPubKey: String) {
        val previousReq = contactRequestManager.get(fromUsername)
        if (previousReq != null) return
        contactRequestManager.add(fromUsername, fromPubKey)
    }

    override fun onContactAcceptReceived(fromUsername: String, fromPubKey: String) {
        val pending = contactRequestManager.get(fromUsername)
        if (pending != null) contactRequestManager.remove(pending)
        viewModelScope.launch { contactsManager.addContact(fromUsername, fromPubKey) }
    }

    // --- success callbacks ---

    override fun onContactRequestSuccess(toUsername: String) {}

    override fun onContactAcceptSuccess(toUsername: String) {
        val request = contactRequestManager.get(toUsername) ?: return
        viewModelScope.launch { contactsManager.addContact(toUsername, request.pubKey) }
        contactRequestManager.remove(request)
    }

    override fun onMessageSuccess(timestamp: Long, toUsername: String, content: String) {
        viewModelScope.launch { conversationManager.flushOutgoing(timestamp) }
    }

    // --- failure callbacks ---

    override fun onChatRequestFailure(toUsername: String, reason: String) {
        error.postValue(reason)
    }

    override fun onChatAcceptFailure(toUsername: String, reason: String) {
        error.postValue(reason)
    }

    override fun onMessageFailure(timestamp: Long, toUsername: String, reason: String) {
        conversationManager.removeOutgoing(timestamp)
        error.postValue(reason)
    }
}
