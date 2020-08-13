package com.example.contactbook.screens.contacts

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactbook.R
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.databinding.ContactsListItemBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.contacts_list_item.view.*

class ContactsAdapter(val listenerExtras: (ContactExtras) -> Unit, val listener: (Contact) -> Unit, val longListener: (Contact) -> Unit)
    : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {


    private var list: List<Contact> = emptyList()
    private var extrasList: List<ContactExtras> = emptyList()

    companion object : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return  oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return  oldItem.contactId == newItem.contactId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactsListItemBinding.inflate(inflater, parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact: Contact = list[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getContactAt(position:Int): Contact {
        return list[position]
    }

    fun setList(contactsList: List<Contact>){
        list = contactsList
        Log.i("ContactsAdapter", list.toString())
        notifyDataSetChanged()
    }

    fun setSecondList(contactsExtrasList: List<ContactExtras>){
        extrasList = contactsExtrasList
        Log.i("ContactsAdapter", extrasList.toString())
        //notifyDataSetChanged()
    }

    inner class ContactViewHolder(private val binding: ContactsListItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        private var nameView: TextView? = null
        private var contactView: TextView? = null
        private var contact: Contact? = null
        private var extras: List<ContactExtras> = emptyList()
        private lateinit var secondList: RecyclerView


        init {
            nameView = itemView.contact_name
            contactView= itemView.contact_number
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        private fun chooseExtras(contact: Contact) {
            extras = extrasList.filter{it.contactOwnerId == contact.contactId}
        }

        fun bind(contact: Contact) {
            binding.contactName.tag = contact.contactId
            binding.contactName.text = contact.name
            binding.contactNumber.text = contact.number
            if(contact.imageUri.isBlank()){
                binding.contactImageView.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.person_icon_24))
            }
            else
                Picasso.get().load(Uri.parse(contact.imageUri)).placeholder(R.drawable.person_icon_24).error(R.drawable.person_icon_24).into(binding.contactImageView)
            if(contact.favourite == 1)
                binding.favouriteImage.visibility = View.VISIBLE
            else
                binding.favouriteImage.visibility = View.INVISIBLE
            this.contact = contact
            secondList = binding.contactsSecondList
            chooseExtras(contact)

            var _adapter = ContactsExtrasAdapter{
                listenerExtras(it)
            }

            secondList.apply{
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = _adapter
                (adapter as ContactsExtrasAdapter).setList(extras)
            }

            binding.editContactButton.setOnClickListener(){
                listener(contact)
            }

        }

        override fun onClick(p0: View?) {
           contact?.let {
               Log.i("ContactsAdapter", contact.toString())
               if(binding.contactsSecondList.visibility == View.GONE)
                   binding.contactsSecondList.visibility = View.VISIBLE
               else
                   binding.contactsSecondList.visibility = View.GONE
           }
        }

        override fun onLongClick(p0: View?): Boolean {
            contact?.let{
                longListener(it)
            }
            return true
        }
    }
}



