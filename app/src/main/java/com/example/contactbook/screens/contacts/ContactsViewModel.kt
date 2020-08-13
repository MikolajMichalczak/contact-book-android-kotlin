package com.example.contactbook.screens.contacts

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.network.RepoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsViewModel (application: Application) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "ContactsViewModel"
    }

    private val repository: ContactsRepository

    private var allContacts: LiveData<List<Contact>>
    var allContactsExtras: LiveData<List<ContactExtras>>

    var _shouldShowFavouritesOnly = MutableLiveData<Boolean>(false)
    val shouldShowFavouritesOnly: LiveData<Boolean>
        get() = _shouldShowFavouritesOnly

    var _isSorted = MutableLiveData<Boolean>(false)
    val isSorted: LiveData<Boolean>
        get() = _isSorted

    private val contactsMediator: MediatorLiveData<Any>
    val contacts: LiveData<List<Contact>>

    private var _toEditContactFragment = MutableLiveData<Boolean>()
    val toEditContactFragment: LiveData<Boolean>
        get() = _toEditContactFragment

    init{
        _toEditContactFragment.value = false
        val database = ContactsRoomDatabase.getDatabase(application, viewModelScope)
        val service = RepoApi.retrofitService
        repository = ContactsRepository(database ,service)
        allContacts = repository.allContacts
        allContactsExtras = repository.allContactsExtras

        contactsMediator = MediatorLiveData<Any>().apply {
            addSource(allContacts) {value = it}
            addSource(shouldShowFavouritesOnly) { value = it }
            addSource(isSorted) { value = it }
        }

        contacts = Transformations.map(contactsMediator) {
            val filteredContacts = (allContacts.value ?: emptyList())
                .filter { if(shouldShowFavouritesOnly.value == true) it.favourite == 1 else true }

            if(isSorted.value == true) filteredContacts.sortedBy { it.name }
            else filteredContacts
        }


    }

    fun removeContacts() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteContact(contact)
    }

    fun toEditContactFragment(){
        _toEditContactFragment.value = true
    }

    fun endNavigateToEditContactFragment(){
        _toEditContactFragment.value = false
    }

    fun update(contact: Contact)= viewModelScope.launch(Dispatchers.IO) {
        repository.updateContact(contact)
    }

    fun getAndDeleteContactFromNotif(contactId: Int) = viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminderByContactId(contactId)
    }

    override fun onCleared() {
        super.onCleared()
    }
}

