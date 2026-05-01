package com.example.ciphrchat.ui_layer.main.contact_request

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.ContactRequest
import com.example.ciphrchat.ui_layer.main.MainActivityViewModel


class ContactRequestsFragment : Fragment(), ContactRequestAdapter.ContactsRequestListener {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ContactRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_contact_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactRequestAdapter(this)

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.fragmentContactRequests_recyclerView_contactRequests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.contactRequestManager.contactRequests.observe(viewLifecycleOwner) { requests ->
            adapter.submitList(requests)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onContactRequestsViewed()
    }

    override fun onContactAccepted(contact: ContactRequest) {
        viewModel.sendContactAccept(contact.fromUsername)
    }

    override fun onContactDeclined(contact: ContactRequest) {
        viewModel.contactRequestManager.remove(contact)
    }
}
