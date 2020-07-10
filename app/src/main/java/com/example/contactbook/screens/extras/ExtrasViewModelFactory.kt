package com.example.contactbook.screens.extras

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.database.entities.ContactExtras


class ExtrasViewModelFactory(private val application: Application, private val _contactExtras: ContactExtras?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExtrasViewModel::class.java)) {
            return _contactExtras?.let {
                ExtrasViewModel(
                    application,
                    it
                )
            } as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}