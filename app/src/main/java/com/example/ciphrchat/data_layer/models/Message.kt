package com.example.ciphrchat.data_layer.models


class Message(
    val content: String,
    val senderUsername: String,
    val contactUsername: String,
    val sentAt: Long
)