package com.example.contactbook.screens.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.contactbook.data.RepoBoundaryCallback
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.network.RepoApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class RepositoryViewModel (application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "RepositoryViewModel"
        private const val PREFS_NAME = "Paging"
    }

    private val sharedPref: SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var filterText = MutableLiveData<String>("")

    private var repoBoundaryCallback: RepoBoundaryCallback? = null

    var reposList: LiveData<PagedList<Repository>>? = null

    private val repository: ContactsRepository

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main
    )

    init {
        val contactsDao =
            ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsDao()
        val contactsExtrasDao =
            ContactsRoomDatabase.getDatabase(application, viewModelScope).contactsExtrasDao()
        val repositoriesDao =
            ContactsRoomDatabase.getDatabase(application, viewModelScope).repositoriesDao()
        val service = RepoApi.retrofitService
        repository = ContactsRepository(contactsDao, contactsExtrasDao, repositoriesDao, service)

        initializePageList(application)
    }

    private val pagedListConfig = PagedList.Config.Builder()
        //.setPrefetchDistance(5)
        //.setInitialLoadSizeHint(20)
        .setEnablePlaceholders(true)
        .setPageSize(15).build()

    private fun initializePageList(application: Application) {
        repoBoundaryCallback = RepoBoundaryCallback(repository, application)

        reposList = Transformations.switchMap(filterText){text ->
            if(text.isNullOrBlank()){
                saveReposType("all_repos", 1)
                LivePagedListBuilder(
                    repository.getPagedRepos(), pagedListConfig
                ).setBoundaryCallback(repoBoundaryCallback).build()
            }
            else{
                repoBoundaryCallback!!.filterText = text
                saveReposType("all_repos", 0)
                LivePagedListBuilder(
                    repository.getSearchedRepos(text), pagedListConfig
                ).setBoundaryCallback(repoBoundaryCallback).build()
            }
        }
    }

    private fun saveReposType(KEY_NAME: String, value: Int){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.commit()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
