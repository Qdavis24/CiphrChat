package com.example.ciphrchat.ui_layer.main.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.Contact
import com.example.ciphrchat.ui_layer.main.MainActivityViewModel

class ContactsFragment : Fragment(), ContactsAdapter.ContactsListener {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactsAdapter(this)

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.fragmentContacts_recyclerView_contacts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.contactsManager.contacts.observe(viewLifecycleOwner) { contacts ->
            adapter.submitList(contacts)
        }
    }

    override fun onContactClicked(contact: Contact) {
        findNavController().navigate(
            R.id.action_contactsFragment_to_conversationFragment,
            Bundle().apply { putString("peerUsername", contact.username) })
    }
}