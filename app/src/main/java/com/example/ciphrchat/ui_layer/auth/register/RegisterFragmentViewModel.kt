package com.example.ciphrchat.ui_layer.auth.register

import androidx.lifecycle.ViewModel
import com.example.ciphrchat.data_layer.models.Session
import com.example.ciphrchat.data_layer.repositories.SessionRepository
import com.example.ciphrchat.data_layer.repositories.UserRepository
import com.example.ciphrchat.services.AuthApiService
import com.example.ciphrchat.utils.CryptoUtils

class RegisterFragmentViewModel : ViewModel() {

    enum class RegisterResult {
        SUCCESS, USERNAME_TAKEN, SERVER_ERROR, LOCAL_ERROR
    }

    suspend fun register(username: String, password: String): RegisterResult {
        val response =
            AuthApiService.register(username, password) ?: return RegisterResult.SERVER_ERROR
        if (response.token == null) return RegisterResult.USERNAME_TAKEN

        val (pubKeyBase64, privKeyBase64) = CryptoUtils.generateKeyPair()

        val saved = UserRepository.saveUser(username, pubKeyBase64, privKeyBase64)
        if (!saved) return RegisterResult.LOCAL_ERROR

        SessionRepository.load(
            Session(
                username = username,
                jwt = response.token,
                publicKey = pubKeyBase64,
                privateKey = privKeyBase64
            )
        )

        return RegisterResult.SUCCESS
    }
}
