package com.example.contactbook.screens.editcontact

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.database.entities.Contact

class EditContactViewModelFactory(private val application: Application, private val _contact: Contact?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditContactViewModel::class.java)) {
            return _contact?.let {
                EditContactViewModel(
                    application,
                    it
                )
            } as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}