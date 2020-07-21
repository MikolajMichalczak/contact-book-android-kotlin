package com.example.contactbook.screens.contacts

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.database.entities.ContactExtras
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
    private val allFavouriteContacts: LiveData<List<Contact>>

    val contacts = MediatorLiveData<List<Contact>>()
    private var currentType = "notFavourite"

    private var _toEditContactFragment = MutableLiveData<Boolean>()
    val toEditContactFragment: LiveData<Boolean>
        get() = _toEditContactFragment

    init{
        _toEditContactFragment.value = false
        val contactsDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsDao()
        val contactsExtrasDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsExtrasDao()
        val repositoriesDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).repositoriesDao()
        val service = RepoApi.retrofitService
        repository = ContactsRepository(contactsDao,contactsExtrasDao, repositoriesDao, service)
        allContacts = repository.allContacts
        allContactsExtras = repository.allContactsExtras
        allFavouriteContacts = repository.allFavouriteContats

        contacts.addSource(allContacts) { result ->
            if (currentType == "notFavourite") {
                result?.let { contacts.value = it }
            }
        }
        contacts.addSource(allFavouriteContacts) { result ->
            if (currentType == "favourite") {
                result?.let { contacts.value = it }
            }
        }
    }

    fun setContactsType(type: String) = when (type) {
        "favourite" -> allFavouriteContacts.value?.let { contacts.value = it }
        else -> allContacts.value?.let { contacts.value = it }
    }.also { currentType = type }

    fun removeContacts() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun sortContacts(){
        allContacts = repository.allAlphabetizedContacts
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

    override fun onCleared() {
        super.onCleared()
    }
}

