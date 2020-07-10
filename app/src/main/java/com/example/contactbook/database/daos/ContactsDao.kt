package com.example.contactbook.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contactbook.database.entities.Contact


@Dao
interface  ContactsDao {

    @Query("SELECT * from contacts_table ORDER BY name ASC")
    fun getAlphabetizedNames(): LiveData<List<Contact>>

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

}