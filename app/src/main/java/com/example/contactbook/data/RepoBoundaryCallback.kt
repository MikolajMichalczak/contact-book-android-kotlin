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

    var filterText = ""

    private var callbackJob = Job()

    private val coroutineScope = CoroutineScope(
        callbackJob + Dispatchers.Main )

    private val PREFS_NAME = "Paging"

    private val sharedPref: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun onZeroItemsLoaded() {
        val reposType = getReposType("all_repos")
        val currentPage = 1
        savePage("current_page", currentPage)
        Log.i("RepoBoundaryCallback", "zeroItemsPage:" + currentPage)
        super.onZeroItemsLoaded()
        fetchUsers(currentPage, reposType)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repository) {
        val reposType = getReposType("all_repos")
        val currentPage = getCurrentPage("current_page")
        val nextPage = currentPage + 1
        Log.i("RepoBoundaryCallback", "itemEndPage:" + currentPage)
        super.onItemAtEndLoaded(itemAtEnd)
        fetchUsers(nextPage, reposType)
        savePage("current_page", nextPage)
    }

    private fun fetchUsers(page: Int, reposType: Int) {
        coroutineScope.launch {
            try {
                when(reposType) {
                    1 -> {
                        var newRepos = RepoApi.retrofitService.fetchAllRepos(page)
                        insertRepoToDb(newRepos)
                    }
                    else -> {
                        var newRepos = RepoApi.retrofitService.fetchSearchedRepos(filterText, page)
                        insertRepoToDb(newRepos)
                    }
                }
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

    private fun getReposType(KEY_NAME: String): Int{
        return sharedPref.getInt(KEY_NAME, 0)
    }

    private fun savePage(KEY_NAME: String, value: Int){
        Log.i("RepoBoundaryCallback", value.toString())
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.commit()
    }
}