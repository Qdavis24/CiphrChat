package com.example.ciphrchat.data_layer.models

data class Session(
    val username: String,
    val jwt: String,
    val publicKey: String,
    val privateKey: String
)