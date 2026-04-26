package com.example.ciphrchat.ui_layer.main.online_users

import androidx.lifecycle.MutableLiveData
import com.example.ciphrchat.data_layer.models.OnlineUser

class OnlineUsersManager {
    val usersOnline = MutableLiveData<List<OnlineUser>>(emptyList())

    fun set(users: List<OnlineUser>) {
        usersOnline.value = users
    }

    fun add(username: String) {
        val current = usersOnline.value!!.toMutableList()
        if (current.none { it.username == username }) {
            current.add(OnlineUser(username = username))
            usersOnline.value = current
        }
    }

    fun remove(username: String) {
        val current = usersOnline.value!!.toMutableList()
        current.removeAll { it.username == username }
        usersOnline.value = current
    }
}
