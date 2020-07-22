package com.example.contactbook.database

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.contactbook.data.ReposDataSource
import com.example.contactbook.database.daos.ContactsDao
import com.example.contactbook.database.daos.ContactsExtrasDao
import com.example.contactbook.database.daos.RepositoriesDao
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.network.RepoApi
import com.example.contactbook.network.RepoApiService

class ContactsRepository (private val contactsDao: ContactsDao, private val contactsExtrasDao: ContactsExtrasDao, private val repositoriesDao: RepositoriesDao, private val service: RepoApiService) {

    val allContacts: LiveData<List<Contact>> = contactsDao.getNames()
    val allContactsExtras: LiveData<List<ContactExtras>> = contactsExtrasDao.getExtras()
    val allRepositories: LiveData<List<Repository>> = repositoriesDao.getAllRepos()
    val allAlphabetizedContacts: LiveData<List<Contact>> = contactsDao.getAlphabetizedNames()
    val allFavouriteContats: LiveData<List<Contact>> = contactsDao.getFavouriteContacts()

    //contacts
    /**
     * @return Id of inserted Contact
     */
    suspend fun insert(contact: Contact): Int{
        return contactsDao.insert(contact).toInt()
    }

    suspend fun deleteAll() {
        contactsDao.deleteAll()
    }

    fun deleteContact(contact: Contact) {
        contactsDao.deleteContact(contact)
    }

    fun updateContact(contact: Contact) {
        contactsDao.updateContact(contact)
    }

    //contactsExtras
    suspend fun insertExtras(contactExtras: ContactExtras) {
        contactsExtrasDao.insertExtras(contactExtras)
    }

    suspend fun deleteAllExtras() {
        contactsExtrasDao.deleteAllExtras()
    }

    fun deleteContactExtras(contactExtras: ContactExtras) {
        contactsExtrasDao.deleteContactExtras(contactExtras)
    }

    fun updateContactExtras(contactExtras: ContactExtras) {
        contactsExtrasDao.updateContactExtras(contactExtras)
    }

    //Repositories

    suspend fun fetchPagedRepos(page: Int, perPage: Int): List<Repository>{
        return service.fetchRepos(page, perPage)
    }

    fun getPagedRepos(): DataSource.Factory<Int, Repository> {
        return repositoriesDao.getPagedRepos()
    }

    fun getAllRepos(){
        repositoriesDao.getAllRepos()
    }

    suspend fun insertRepo(repository: Repository) {
        repositoriesDao.insertRepo(repository)
    }

    suspend fun deleteAllRepos() {
        repositoriesDao.deleteAllRepos()
    }

    fun deleteRepo(repository: Repository) {
        repositoriesDao.deleteRepo(repository)
    }

    fun updateRepo(repository: Repository) {
        repositoriesDao.updateRepo(repository)
    }
}