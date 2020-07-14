package com.example.contactbook.screens.editcontact

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.database.entities.ContactExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditContactViewModel(application: Application, _contact: Contact) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "EditContactViewModel"
    }

    private val repository: ContactsRepository
    var contact = _contact
    var nameText:String = ""
    var numberText:String = ""

    private var _toContactsFragment = MutableLiveData<Boolean>()
    val toContactsFragment: LiveData<Boolean>
    get() = _toContactsFragment

    var _imageUri = MutableLiveData<String>()
    val imageUri: LiveData<String>
        get() = _imageUri

    init {
        val contactsDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsDao()
        val contactsExtrasDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsExtrasDao()
        repository = ContactsRepository(contactsDao, contactsExtrasDao)
        if(contact.name.isNotEmpty()) {
            nameText = _contact.name
            numberText = contact.number
            _imageUri.value = contact.imageUri
        }
        else{
            _imageUri.value = ""
        }
    }

    fun updateContact(){
        Log.i(TAG, contact.toString())
        if(contact.name.isNotEmpty()) {
            if(nameText.isNotEmpty()&&numberText.isNotEmpty()) {
                contact.name = nameText
                contact.number = numberText
                contact.imageUri = _imageUri.value.toString()
                update(contact)
                toContactsFragment()
            }
            else
                toContactsFragment()
        }
        else{
            if(nameText.isNotEmpty()&&numberText.isNotEmpty()) {
                val newContact =
                    Contact(
                        0,
                        nameText,
                        numberText,
                        imageUri.value.toString(),
                        0
                    )
                insert(newContact)
                toContactsFragment()
            }
            else
                toContactsFragment()
        }
    }
    private fun update(contact: Contact)= viewModelScope.launch(Dispatchers.IO) {
        repository.updateContact(contact)
    }

    private fun insert(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        val contactID =  repository.insert(contact)
        //Log.i(TAG,contact.id.toString())
        repository.insertExtras(ContactExtras(0, "","", contactID))
    }


    private fun toContactsFragment(){
        _toContactsFragment.value = true
    }

    fun endNavigateToContactsFragment(){
        _toContactsFragment.value = false
    }

}

