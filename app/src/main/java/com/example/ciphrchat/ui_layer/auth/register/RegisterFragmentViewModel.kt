package com.example.ciphrchat.ui_layer.auth.register

import androidx.lifecycle.ViewModel
import com.example.ciphrchat.data_layer.models.Session
import com.example.ciphrchat.data_layer.repositories.SessionRepository
import com.example.ciphrchat.data_layer.repositories.UserRepository
import com.example.ciphrchat.services.AuthApiService
import java.security.KeyPair
import java.security.KeyPairGenerator

// RegisterFragmentViewModel.kt
class RegisterFragmentViewModel : ViewModel() {

    enum class RegisterResult {
        SUCCESS,
        USERNAME_TAKEN,
        SERVER_ERROR,
        LOCAL_ERROR
    }

    suspend fun register(username: String, password: String): RegisterResult {
        val response =
            AuthApiService.register(username, password) ?: return RegisterResult.SERVER_ERROR

        if (response.token == null) return RegisterResult.USERNAME_TAKEN

        val keyPair = generateKeyPair()

        val saved = UserRepository.saveUser(username, keyPair.public.toString(), keyPair.private.toString())
        if (!saved) return RegisterResult.LOCAL_ERROR

        SessionRepository.load(
            Session(
                username = username,
                jwt = response.token,
                publicKey = keyPair.public.toString(),
                privateKey = keyPair.private.toString()
            )
        )

        return RegisterResult.SUCCESS
    }

    private fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }
}