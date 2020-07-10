package com.example.contactbook.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contactbook.database.entities.ContactExtras

@Dao
interface  ContactsExtrasDao {

    @Query("SELECT * from contacts_extras_table")
    fun getExtras(): LiveData<List<ContactExtras>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExtras(contactExtras: ContactExtras)

    @Query("DELETE FROM contacts_table")
    suspend fun deleteAllExtras()

    @Delete
    fun deleteContactExtras(contactExtras: ContactExtras)

    @Update
    fun updateContactExtras(contactExtras: ContactExtras)

}