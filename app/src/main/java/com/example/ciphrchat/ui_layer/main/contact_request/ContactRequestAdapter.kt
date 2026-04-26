package com.example.ciphrchat.ui_layer.main.contact_request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.ContactRequest

class ContactRequestAdapter(val listener: ContactsRequestListener) : RecyclerView.Adapter<ContactRequestAdapter.ViewHolder>() {

    interface ContactsRequestListener {
        fun onContactAccepted(contact: ContactRequest)
        fun onContactDeclined(contact: ContactRequest)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUsername: TextView = view.findViewById(R.id.contactRequestItem_textView_username)
        private val btnContactAccept: ImageButton = view.findViewById(R.id.contactRequestItem_button_accept)
        private val btnContactDeclined: ImageButton = view.findViewById(R.id.contactRequestItem_button_decline)

        fun update(contactReq: ContactRequest) {
            tvUsername.text = contactReq.fromUsername
            btnContactAccept.setOnClickListener { listener.onContactAccepted(contactReq) }
            btnContactDeclined.setOnClickListener { listener.onContactDeclined(contactReq) }
        }
    }

    private var contactReqs = listOf<ContactRequest>()

    fun submitList(newContactRequests: List<ContactRequest>) {
        contactReqs = newContactRequests
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.update(contactReqs[position])
    }

    override fun getItemCount(): Int {
        return contactReqs.size
    }

}