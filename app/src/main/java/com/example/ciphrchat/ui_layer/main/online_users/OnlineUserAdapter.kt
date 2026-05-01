package com.example.ciphrchat.ui_layer.main.online_users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.OnlineUser

class OnlineUsersAdapter(val listener: OnlineUsersListener) :
    RecyclerView.Adapter<OnlineUsersAdapter.ViewHolder>() {
    interface OnlineUsersListener {
        fun onAddContactClicked(user: OnlineUser)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUsername: TextView = view.findViewById(R.id.onlineUserItem_textView_username)
        private val btnAddContact: Button = view.findViewById(R.id.onlineUserItem_button_addContact)

        fun update(user: OnlineUser) {
            tvUsername.text = user.username
            btnAddContact.setOnClickListener { listener.onAddContactClicked(user) }
        }
    }

    private var users = listOf<OnlineUser>()

    fun submitList(newUsers: List<OnlineUser>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_online_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.update(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

}