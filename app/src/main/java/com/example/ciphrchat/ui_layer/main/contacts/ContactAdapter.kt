package com.example.ciphrchat.ui_layer.main.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.Contact

class ContactsAdapter(val listener: ContactsListener) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    interface ContactsListener {
        fun onContactClicked(contact: Contact)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUsername: TextView = view.findViewById(R.id.contactItem_textView_username)

        fun update(contact: Contact) {
            tvUsername.text = contact.username
            itemView.setOnClickListener { listener.onContactClicked(contact) }
        }
    }

    private var contacts = listOf<Contact>()

    fun submitList(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.update(contacts[position])
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

}