package com.example.ciphrchat.ui_layer.main.online_users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.OnlineUser
import com.example.ciphrchat.ui_layer.main.MainActivityViewModel

class OnlineUsersFragment : Fragment(), OnlineUsersAdapter.OnlineUsersListener {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: OnlineUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_online_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OnlineUsersAdapter(this)


        val recyclerView =
            view.findViewById<RecyclerView>(R.id.addContactFragment_recyclerView_onlineUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.onlineUsersManager.usersOnline.observe(viewLifecycleOwner) { users ->
            Log.d(
                "USERS_ONLINE",
                "fragment received ${users.size} users: ${users.map { it.username }}"
            )
            adapter.submitList(users)
        }


    }

    override fun onAddContactClicked(user: OnlineUser) {
        viewModel.sendContactRequest(user.username)
    }
}