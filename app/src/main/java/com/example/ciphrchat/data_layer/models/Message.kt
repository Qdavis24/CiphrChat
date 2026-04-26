package com.example.ciphrchat.data_layer.models

data class Message(
    val messageId: Int,
    val conversationId: Int,
    val content: String,
    val senderUsername: String,
    val sentAt: Long
)