package com.example.contactbook.screens.contacts

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.databinding.ContactsExtrasListItemBinding
import com.example.contactbook.databinding.ContactsListItemBinding
import kotlinx.android.synthetic.main.contacts_extras_list_item.view.*
import kotlinx.android.synthetic.main.contacts_list_item.view.*

class ContactsExtrasAdapter(val listener: (ContactExtras) -> Unit)
    : RecyclerView.Adapter<ContactsExtrasAdapter.ContactExtrasViewHolder>() {

    private var list: List<ContactExtras> = emptyList()

    companion object : DiffUtil.ItemCallback<ContactExtras>() {
        override fun areItemsTheSame(oldItem: ContactExtras, newItem: ContactExtras): Boolean {
            return  oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ContactExtras, newItem: ContactExtras): Boolean {
            return  oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactExtrasViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactsExtrasListItemBinding.inflate(inflater, parent, false)
        return ContactExtrasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactExtrasViewHolder, position: Int) {
        val contactExtras: ContactExtras = list[position]
        holder.bind(contactExtras)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(contactsSecondList:List<ContactExtras>){
        list = contactsSecondList
        notifyDataSetChanged()
    }

    inner class ContactExtrasViewHolder(private val binding: ContactsExtrasListItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var addressView: TextView? = null
        private var emailView: TextView? = null
        private var contactExtras: ContactExtras? = null

        init {
            addressView = itemView.contact_address
            emailView= itemView.contact_email
            itemView.setOnClickListener(this)
        }

        fun bind(contactExtras: ContactExtras) {
            binding.contactAddress.tag = contactExtras.id
            Log.i("ContactsExtrasAdapter", contactExtras.toString())
            binding.contactAddress.text = "Address: " + contactExtras.address
            binding.contactEmail.text = "Email: " + contactExtras.email
            this.contactExtras = contactExtras
        }

        override fun onClick(p0: View?) {
            contactExtras?.let {
                listener(it)
            }
        }
    }
}
