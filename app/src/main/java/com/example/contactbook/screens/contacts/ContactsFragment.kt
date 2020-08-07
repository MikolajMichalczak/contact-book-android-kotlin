package com.example.contactbook.screens.contacts

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
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
    private lateinit var contactsType:String
    private lateinit var menu: Menu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        bundle = Bundle()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.onActionViewCollapsed()
        searchView.setOnQueryTextListener(this)
        this.menu = menu
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
            viewModel._isSorted.value = true
            Toast.makeText(activity, "Sorted", Toast.LENGTH_SHORT).show()
        }

        if (id == R.id.fav_button){
            changeStar()
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
        contactsType = "notFavourite"

        val builder = AlertDialog.Builder(requireContext())

        _adapter = ContactsAdapter(
            listenerExtras = {
            Log.i(TAG, it.toString())
            bundle.putParcelable("contactExtras", it)
            findNavController().navigate(R.id.action_pageContainerFragment_to_extrasFragment2, bundle) },

            listener = {
                bundle.putParcelable("contact", it)
                findNavController().navigate(R.id.action_pageContainerFragment_to_editContactFragment, bundle) },

            longListener = {
                if(it.favourite == 0) {
                    builder.setTitle("Do you want to make contact favourite?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        it.favourite = 1
                        viewModel.update(it)
                        Log.i(TAG, it.toString())
                    }

                    builder.setNegativeButton("Cancel") { dialog, which ->
                        //
                    }
                }
                else{
                    builder.setTitle("Do you want to make contact not favourite?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        it.favourite = 0
                        viewModel.update(it)
                        Log.i(TAG, it.toString())
                    }

                    builder.setNegativeButton("Cancel") { dialog, which ->
                        //
                    }
                }
                builder.show()
            }
        )

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

            viewModel.contacts.observe(viewLifecycleOwner, Observer { contactsList->
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

    private fun changeStar(){
        if(viewModel.shouldShowFavouritesOnly.value == false) {
            viewModel._shouldShowFavouritesOnly.value = true
            menu.getItem(2).setIcon(ContextCompat.getDrawable(context!!, R.drawable.star_icon_yellow_24));
        }
        else{
            viewModel._shouldShowFavouritesOnly.value = false
            menu.getItem(2).setIcon(ContextCompat.getDrawable(context!!, R.drawable.star_icon_white_24));
        }
    }

    private fun navigateToEditContactFragment(_state: Boolean) {
        if(_state) {
            bundle.putParcelable("contact",
                Contact(0, "", "", "", 0)
            )
            findNavController().navigate(
                R.id.action_pageContainerFragment_to_editContactFragment,bundle
            )
            viewModel.endNavigateToEditContactFragment()
        }
    }

    override fun onPause() {
        super.onPause()
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.onActionViewCollapsed()
    }

}


