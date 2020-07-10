package com.example.contactbook.screens.contacts

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactbook.R
import com.example.contactbook.SwipeToDeleteCallback
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.databinding.FragmentContactsBinding


class ContactsFragment : Fragment(), SearchView.OnQueryTextListener {

    companion object{
        private const val TAG = "ContactsFragment"
    }

    private lateinit var viewModel: ContactsViewModel
    private lateinit var binding: FragmentContactsBinding
    private lateinit var bundle: Bundle
    private lateinit var contactsListForSearch: List<Contact>
    private lateinit var _adapter: ContactsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        bundle = Bundle()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        val filteredModelList: List<Contact> = filter(contactsListForSearch, query)
        _adapter.setList(filteredModelList)
        binding.contactsList.scrollToPosition(0)
        return true
    }

    private fun filter(contactListForSearch: List<Contact>, query: String?): List<Contact>{
        val  lowerCaseQuery = query?.toLowerCase();
        val filteredContactsList: MutableList<Contact> = ArrayList()
        for (contact in contactListForSearch) {
            val text: String = contact.name.toLowerCase()
            if (text.contains(lowerCaseQuery.toString())) {
                filteredContactsList.add(contact)
            }
        }
        return filteredContactsList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item!!.itemId

        if (id == R.id.action_delete){
            viewModel.removeContacts()
            Toast.makeText(activity, "removed", Toast.LENGTH_SHORT).show()
        }
        if (id == R.id.action_sort){
            viewModel.sortContacts()
            Toast.makeText(activity, "Sorted", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_contacts, container, false)

            viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
            binding.viewModel = viewModel
            binding.lifecycleOwner = this

            var contactList = binding.contactsList

                _adapter = ContactsAdapter(listenerExtras = {contactExtras->
                Log.i(TAG, contactExtras.toString())
                bundle.putParcelable("contactExtras", contactExtras)
                findNavController().navigate(R.id.action_contactsFragment_to_extrasFragment, bundle) },

                listener = {contact ->
                    bundle.putParcelable("contact", contact)
                    findNavController().navigate(R.id.action_contactsFragment_to_editContactFragment, bundle)
                })

            contactList.apply{
                layoutManager = LinearLayoutManager(activity)
                adapter = _adapter
            }

        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteContact(_adapter.getContactAt(viewHolder.adapterPosition))
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(contactList)


            viewModel.allContactsExtras.observe(viewLifecycleOwner, Observer { extrasList->
                _adapter.setSecondList(extrasList)
            })

            viewModel.allContacts.observe(viewLifecycleOwner, Observer { contactsList->
                contactsListForSearch = contactsList
                _adapter.setList(contactsList)
                if(contactList.adapter?.itemCount == 0)
                    binding.emptyView.visibility = View.VISIBLE
                else
                    binding.emptyView.visibility = View.GONE
            })

            viewModel.toEditContactFragment.observe(viewLifecycleOwner, Observer {state ->
                navigateToEditContactFragment(state)
            })

            return binding.root
        }

    private fun navigateToEditContactFragment(_state: Boolean) {
        if(_state) {
            bundle.putParcelable("contact",
                Contact(0, "", "")
            )
            findNavController().navigate(
                R.id.action_contactsFragment_to_editContactFragment,
                bundle
            )
            viewModel.endNavigateToEditContactFragment()
        }
    }

}


