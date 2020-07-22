package com.example.contactbook.screens.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.network.RepoApi
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.data.RepoBoundaryCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RepositoryViewModel (application: Application) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "RepositoryViewModel"
    }

    //var reposList: LiveData<PagedList<Repository>>

    private var repoBoundaryCallback: RepoBoundaryCallback? = null

    var reposList: LiveData<PagedList<Repository>>? = null

    private val repository: ContactsRepository

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main )

    init {
        val contactsDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsDao()
        val contactsExtrasDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsExtrasDao()
        val repositoriesDao = ContactsRoomDatabase.getDatabase(application, viewModelScope).repositoriesDao()
        val service = RepoApi.retrofitService
        repository = ContactsRepository(contactsDao, contactsExtrasDao, repositoriesDao, service)
        initializedPagedListBuilder(application)
    }


    private fun initializedPagedListBuilder(application: Application) {
        repoBoundaryCallback = RepoBoundaryCallback(
            repository, application
        )

        val pagedListConfig = PagedList.Config.Builder()
            //.setPrefetchDistance(5)
            //.setInitialLoadSizeHint(20)
            .setEnablePlaceholders(true)
            .setPageSize(15).build()

        reposList = LivePagedListBuilder(
            repository.getPagedRepos(),
            pagedListConfig
        ).setBoundaryCallback(repoBoundaryCallback).build()
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
