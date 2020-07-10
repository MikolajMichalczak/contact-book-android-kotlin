package com.example.contactbook.screens.contacts

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.database.entities.ContactExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsViewModel (application: Application) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "ContactsViewModel"
    }

    private val repository: ContactsRepository

    var allContacts: LiveData<List<Contact>>
    var allContactsExtras: LiveData<List<ContactExtras>>

    private var _toEditContactFragment = MutableLiveData<Boolean>()
    val toEditContactFragment: LiveData<Boolean>
        get() = _toEditContactFragment

    init{
        _toEditContactFragment.value = false
        val contactsDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsDao()
        val contactsExtrasDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsExtrasDao()
        repository = ContactsRepository(contactsDao,contactsExtrasDao)
        allContacts = repository.allContacts
        allContactsExtras = repository.allContactsExtras
    }

    fun removeContacts() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun sortContacts(){
        allContacts = repository.allAlphabetizedContacts
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteContact(contact)
        //updateLists()
    }

//    private fun updateLists() = viewModelScope.launch(Dispatchers.IO) {
//        allContactsExtras = repository.allContactsExtras
//        allContacts = repository.allContacts
//
//    }

    fun deleteContactExtras(contactExtras: ContactExtras) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteContactExtras(contactExtras)
    }

    fun toEditContactFragment(){
        _toEditContactFragment.value = true
    }

    fun endNavigateToEditContactFragment(){
        _toEditContactFragment.value = false
    }

    override fun onCleared() {
        super.onCleared()
    }
}

