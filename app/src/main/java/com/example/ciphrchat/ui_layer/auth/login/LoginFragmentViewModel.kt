package com.example.ciphrchat.ui_layer.auth.login

import androidx.lifecycle.ViewModel
import com.example.ciphrchat.data_layer.models.Session
import com.example.ciphrchat.data_layer.repositories.SessionRepository
import com.example.ciphrchat.data_layer.repositories.UserRepository
import com.example.ciphrchat.services.AuthApiService

class LoginFragmentViewModel : ViewModel() {
    enum class AuthResult {
        SUCCESS, INVALID_CREDENTIALS, SERVER_ERROR, LOCAL_ERROR
    }

    suspend fun getUsername(): String? {
        return UserRepository.getUser()?.username
    }

    suspend fun login(username: String, password: String): AuthResult {
        val response = AuthApiService.login(username, password) ?: return AuthResult.SERVER_ERROR
        val token = response.token ?: return AuthResult.INVALID_CREDENTIALS
        val user = UserRepository.getUser() ?: return AuthResult.LOCAL_ERROR

        SessionRepository.load(
            Session(
                username = user.username,
                jwt = token,
                publicKey = user.publicKey,
                privateKey = user.privateKey
            )
        )

        return AuthResult.SUCCESS
    }
}