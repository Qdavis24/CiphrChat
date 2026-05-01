package com.example.ciphrchat.services

import com.example.ciphrchat.data_layer.repositories.SessionRepository
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketService(private val listener: SocketListener) {

    interface SocketListener {
        fun onConnected()

        // broadcasts
        fun onUserOnline(username: String)
        fun onUserOffline(username: String)

        // --- received from other client ---
        fun onMessageReceived(fromUsername: String, content: String)
        fun onContactRequestReceived(fromUsername: String, fromPubKey: String)
        fun onContactAcceptReceived(fromUsername: String, fromPubKey: String)

        // --- success acks ---
        fun onContactRequestSuccess(toUsername: String)
        fun onContactAcceptSuccess(toUsername: String)
        fun onMessageSuccess(timestamp: Long, toUsername: String, content: String)

        // --- failure acks ---
        fun onChatRequestFailure(toUsername: String, reason: String)
        fun onChatAcceptFailure(toUsername: String, reason: String)
        fun onMessageFailure(timestamp: Long, toUsername: String, reason: String)
    }

    private lateinit var socket: Socket

    // we need this to access the main thread
    // event callbacks occur on a special thread managed by the Socket IO library
    // we must ensure to call our handlers (implementations of the interface) on the main thread because they all touch ui
    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())

    fun connect() {
        val jwt = SessionRepository.session.jwt
        val options = IO.Options().apply {
            query = "token=$jwt"
        }
        socket = IO.socket("http://10.0.2.2:5000", options)
        registerEvents()
        socket.connect()
    }

    fun disconnect() {
        if (::socket.isInitialized) {
            socket.disconnect()
        }
    }

    fun announceOnline() {
        socket.emit("announce_online")
    }

    fun sendContactRequest(toUsername: String, fromPubKey: String) {
        val data = JSONObject().apply {
            put("to_username", toUsername)
            put("from_pubkey", fromPubKey)
        }
        socket.emit("contact_request", data, Ack { args ->
            val response = args[0] as JSONObject
            if (response.getString("status") == "ok") {
                mainHandler.post { listener.onContactRequestSuccess(toUsername) }
            } else {
                mainHandler.post {
                    listener.onChatRequestFailure(
                        toUsername, response.optString("reason")
                    )
                }
            }
        })
    }

    fun sendContactAccept(toUsername: String, fromPubKey: String) {
        val data = JSONObject().apply {
            put("to_username", toUsername)
            put("from_pubkey", fromPubKey)
        }
        socket.emit("contact_accept", data, Ack { args ->
            val response = args[0] as JSONObject
            if (response.getString("status") == "ok") {
                mainHandler.post { listener.onContactAcceptSuccess(toUsername) }
            } else {
                mainHandler.post {
                    listener.onChatAcceptFailure(
                        toUsername, response.optString("reason")
                    )
                }
            }
        })
    }

    fun sendMessage(timestamp: Long, toUsername: String, content: String) {
        val data = JSONObject().apply {
            put("to_username", toUsername)
            put("content", content)
        }
        socket.emit("send_message", data, Ack { args ->
            val response = args[0] as JSONObject
            if (response.getString("status") == "ok") {
                mainHandler.post { listener.onMessageSuccess(timestamp, toUsername, content) }
            } else {
                mainHandler.post {
                    listener.onMessageFailure(
                        timestamp, toUsername, response.optString("reason")
                    )
                }
            }
        })
    }

    private fun registerEvents() {
        socket.on(Socket.EVENT_CONNECT) {
            mainHandler.post { listener.onConnected() }
        }

        socket.on("user_online") { args ->
            val data = args[0] as JSONObject
            val username = data.getString("username")
            mainHandler.post { listener.onUserOnline(username) }
        }

        socket.on("user_offline") { args ->
            val data = args[0] as JSONObject
            mainHandler.post { listener.onUserOffline(data.getString("username")) }
        }

        socket.on("message") { args ->
            val data = args[0] as JSONObject
            mainHandler.post {
                listener.onMessageReceived(
                    data.getString("from_username"), data.getString("content")
                )
            }
        }

        socket.on("contact_request") { args ->
            val data = args[0] as JSONObject
            val fromUsername = data.getString("from_username")
            val pubKey = data.getString("from_pubkey")
            mainHandler.post { listener.onContactRequestReceived(fromUsername, pubKey) }
        }

        socket.on("contact_accept") { args ->
            val data = args[0] as JSONObject
            val fromUsername = data.getString("from_username")
            val pubKey = data.getString("from_pubkey")
            mainHandler.post { listener.onContactAcceptReceived(fromUsername, pubKey) }
        }
    }
}