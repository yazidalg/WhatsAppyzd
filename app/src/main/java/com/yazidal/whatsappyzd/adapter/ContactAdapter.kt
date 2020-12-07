package com.yazidal.whatsappyzd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yazidal.whatsappyzd.R
import com.yazidal.whatsappyzd.data.Contact
import com.yazidal.whatsappyzd.listener.ContactListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_contact.*

class ContactAdapter(val contact: ArrayList<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactsViewHolder>() {

    private var clickListener: ContactListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactsViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_contact,
            parent, false
        )
    )
    override fun getItemCount() = contact.size // menghitung jumlah kontak yang didapat

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bindItem(contact[position], clickListener)
    }

    class ContactsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindItem(contact: Contact, listener: ContactListener?) {
            txt_contact_name.text = contact.name
            txt_contact_number.text = contact.phone
            itemView.setOnClickListener {
                listener?.onContactClicked(contact.name, contact.phone)
            }
        }
    }

    fun setOnItemClickListener(listener: ContactListener) {
        clickListener = listener
        notifyDataSetChanged()
    }
}

