package com.example.contactbook.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.screens.repository.RepositoryViewModel
import kotlinx.coroutines.*

class ReposDataSource(private val contactsRepository: ContactsRepository,
                            private val scope: CoroutineScope, application: Application): PageKeyedDataSource<Int, Repository>() {

    private var supervisorJob = SupervisorJob()

    private val PREFS_NAME = "Paging"

    private val sharedPref: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Repository>
    ) {
        Log.i("RepoBoundaryCallback", "initialTriggered")
        val currentPage = 1
        val nextPage = currentPage + 1

        executeQuery(currentPage, params.requestedLoadSize) {
            callback.onResult(it, null, nextPage)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) {

        val currentPage = params.key
        val nextPage = currentPage + 1

        executeQuery(currentPage, params.requestedLoadSize) {
            callback.onResult(it, nextPage)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) {
//        val page = params.key
//        executeQuery(page, params.requestedLoadSize) {
//            callback.onResult(it, page - 1)
//        }
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }

    private fun executeQuery(page: Int, perPage: Int, callback: (List<Repository>) -> Unit) {
        scope.launch(getJobErrorHandler() + supervisorJob) {
            savePage("current_page", page)
            val repos = contactsRepository.fetchPagedRepos("", page, perPage)
            callback(repos)
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(ReposDataSource::class.java.simpleName, "An error happened: $e")
       // networkState.postValue(NetworkState.FAILED)
    }

    private fun savePage(KEY_NAME: String, value: Int){
        Log.i("RepoBoundaryCallback", value.toString())
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.commit()
    }
}