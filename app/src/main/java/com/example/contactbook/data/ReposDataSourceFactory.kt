package com.example.contactbook.data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.entities.Repository
import kotlinx.coroutines.CoroutineScope

class ReposDataSourceFactory (private val contactsRepository: ContactsRepository,
                              private val scope: CoroutineScope, private val application: Application
): DataSource.Factory<Int, Repository>() {

    val source = MutableLiveData<ReposDataSource>()

    override fun create(): DataSource<Int, Repository> {
        val source = ReposDataSource(contactsRepository, scope, application)
        this.source.postValue(source)
        return source
    }
}