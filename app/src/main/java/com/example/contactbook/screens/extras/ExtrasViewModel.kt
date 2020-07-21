package com.example.contactbook.screens.extras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.network.RepoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExtrasViewModel (application: Application, _contactExtras: ContactExtras) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "EditContactViewModel"
    }

    private val repository: ContactsRepository
    var contactExtras = _contactExtras
    var addressText:String = ""
    var emailText:String = ""

    private var _toContactsFragment = MutableLiveData<Boolean>()
    val toContactsFragment: LiveData<Boolean>
        get() = _toContactsFragment

    init {
        val contactsDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsDao()
        val contactsExtrasDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsExtrasDao()
        val repositoriesDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).repositoriesDao()
        val service = RepoApi.retrofitService
        repository = ContactsRepository(contactsDao, contactsExtrasDao, repositoriesDao, service)
        if(contactExtras.address.isNotEmpty()) {
            addressText = contactExtras.address
            emailText = contactExtras.email
        }
    }

    fun updateContact() {
        contactExtras.address = addressText
        contactExtras.email = emailText
        update(contactExtras)
        toContactsFragment()
    }

    private fun update(contactExtras: ContactExtras)= viewModelScope.launch(Dispatchers.IO) {
        repository.updateContactExtras(contactExtras)
    }

    private fun toContactsFragment(){
        _toContactsFragment.value = true
    }

    fun endNavigateToContactsFragment(){
        _toContactsFragment.value = false
    }

}
