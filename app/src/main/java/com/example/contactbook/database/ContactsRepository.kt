package com.example.contactbook.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.contactbook.database.daos.ContactsDao
import com.example.contactbook.database.daos.ContactsExtrasDao
import com.example.contactbook.database.daos.RepositoriesDao
import com.example.contactbook.database.entities.CallReminder
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.database.entities.combined.ContactAndCallReminder
import com.example.contactbook.network.RepoApiService

class ContactsRepository (private val database: ContactsRoomDatabase, private val service: RepoApiService) {

    val allContacts: LiveData<List<Contact>> = database.contactsDao().getNames()
    val allContactsExtras: LiveData<List<ContactExtras>> = database.contactsExtrasDao().getExtras()
    //val Reminder: LiveData<CallReminder> = database.callRemindersDao().getReminder(name = String())

    //contacts
    /**
     * @return Id of inserted Contact
     */
    suspend fun insertContact(contact: Contact): Int{
        return database.contactsDao().insert(contact).toInt()
    }

    suspend fun getContactById(contactId: Int): Contact{
        return database.contactsDao().getContactById(contactId)
    }

    suspend fun deleteAll() {
        database.contactsDao().deleteAll()
    }

    fun deleteContact(contact: Contact) {
        database.contactsDao().deleteContact(contact)
    }

    fun updateContact(contact: Contact) {
        database.contactsDao().updateContact(contact)
    }

    //contactsExtras
    suspend fun insertExtras(contactExtras: ContactExtras) {
        database.contactsExtrasDao().insertExtras(contactExtras)
    }

    suspend fun deleteAllExtras() {
        database.contactsExtrasDao().deleteAllExtras()
    }

    fun deleteContactExtras(contactExtras: ContactExtras) {
        database.contactsExtrasDao().deleteContactExtras(contactExtras)
    }

    fun updateContactExtras(contactExtras: ContactExtras) {
        database.contactsExtrasDao().updateContactExtras(contactExtras)
    }

    //Repositories

    suspend fun fetchPagedRepos(name: String, page: Int, perPage: Int): List<Repository>{
        return service.fetchSearchedRepos(name)
    }


    fun getSearchedRepos(name: String): DataSource.Factory<Int, Repository> {
        return database.repositoriesDao().getSearchedRepos(name)
    }

    fun getPagedRepos(): DataSource.Factory<Int, Repository> {
        return database.repositoriesDao().getPagedRepos()
    }

    suspend fun insertRepo(repository: Repository) {
        database.repositoriesDao().insertRepo(repository)
    }

    suspend fun insertRepos(repositories: Iterable<Repository>) {
        database.repositoriesDao().insertRepos(repositories)
    }

    suspend fun deleteAllRepos() {
        database.repositoriesDao().deleteAllRepos()
    }

    fun deleteRepo(repository: Repository) {
        database.repositoriesDao().deleteRepo(repository)
    }

    fun updateRepo(repository: Repository) {
        database.repositoriesDao().updateRepo(repository)
    }

    //CallReminders

    fun getReminder(contactId: Int): LiveData<CallReminder> {
         return database.callRemindersDao().getReminder(contactId)
    }

    suspend fun insertReminder(reminder: CallReminder) {
        database.callRemindersDao().insert(reminder)
    }

    suspend fun deleteAllReminders() {
        database.callRemindersDao().deleteAll()
    }

    //ContactAndCallReminder

    fun getContactsAndCallReminder(): LiveData<List<ContactAndCallReminder>> {
        return database.contactsDao().getContactsAndCallReminder()
    }

    fun getContactAndCallReminderById(id: Int): LiveData<ContactAndCallReminder> {
        return database.contactsDao().getContactAndCallReminderById(id)
    }

    fun deleteReminder(callReminder: CallReminder){
        database.callRemindersDao().deleteReminder(callReminder)
    }

    fun deleteReminderByContactId(contactId: Int){
        database.callRemindersDao().deleteReminderBYContactId(contactId)
    }

}