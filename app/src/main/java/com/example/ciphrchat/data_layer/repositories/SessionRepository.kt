package com.example.ciphrchat.data_layer.repositories

import com.example.ciphrchat.data_layer.models.Session

object SessionRepository {
    private var _session: Session? = null

    val session: Session
        get() = _session ?: throw IllegalStateException("No active session")

    val isActive: Boolean
        get() = _session != null

    fun load(session: Session) {
        _session = session
    }

    fun clear() {
        _session = null
    }
}