package com.example.contactbook.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.paging.PagedList
import com.example.contactbook.database.ContactsRepository
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.network.RepoApi
import com.example.contactbook.network.RepoApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class RepoBoundaryCallback (val repository: ContactsRepository, application: Application) :
    PagedList.BoundaryCallback<Repository?>() {


    private var callbackJob = Job()

    private val coroutineScope = CoroutineScope(
        callbackJob + Dispatchers.Main )

    private val PREFS_NAME = "Paging"

    private val sharedPref: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun onZeroItemsLoaded() {
        Log.i("RepoBoundaryCallback", "onzeroitemstriggered")
        super.onZeroItemsLoaded()
        fetchUsers(1)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repository) {
        Log.i("RepoBoundaryCallback", "onitematendriggered")
        super.onItemAtEndLoaded(itemAtEnd)
        fetchUsers(getCurrentPage("current_page"))
    }

    private fun fetchUsers(page: Int) {
        coroutineScope.launch {
            try {
                var newRepos = RepoApi.retrofitService.fetchRepos(page)
                insertRepoToDb(newRepos)
            }
            catch (e: Exception){
                Log.i("RepoBoundaryCallback", e.toString())
            }
        }
    }

     private suspend fun insertRepoToDb(reposList: List<Repository>){
        reposList.forEach{repository.insertRepo(it)}
    }

    private fun getCurrentPage(KEY_NAME: String): Int{
        return sharedPref.getInt(KEY_NAME, 0)
    }
}