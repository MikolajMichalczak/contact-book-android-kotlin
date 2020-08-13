package com.example.contactbook.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contactbook.database.entities.CallReminder

@Dao
interface  CallRemindersDao {

    @Query("SELECT * from call_reminders_table WHERE contactOwnerId LIKE :id")
    fun getReminder(id: Int): LiveData<CallReminder>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(callReminder:CallReminder)

    @Query("DELETE FROM call_reminders_table")
    suspend fun deleteAll()

    @Delete
    fun deleteReminder(callReminder: CallReminder)

    @Query("DELETE FROM call_reminders_table WHERE contactOwnerId = :contactId")
    fun deleteReminderBYContactId(contactId: Int)

    @Update
    fun updateReminder(callReminder: CallReminder)
}