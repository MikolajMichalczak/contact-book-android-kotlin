package com.example.contactbook.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contactbook.database.entities.Contact


@Dao
interface  ContactsDao {

    @Query("SELECT * from contacts_table")
    fun getNames(): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact): Long

    @Query("DELETE FROM contacts_table")
    suspend fun deleteAll()

    @Delete
    fun deleteContact(contact: Contact)

    @Update
    fun updateContact(contact: Contact)

    @Query("SELECT * from contacts_table where favourite = 1")
    fun getFavouriteContacts(): LiveData<List<Contact>>

}