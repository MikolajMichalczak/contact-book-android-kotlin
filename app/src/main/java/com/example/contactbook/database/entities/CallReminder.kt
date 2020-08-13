package com.example.contactbook.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.Nullable
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "call_reminders_table", foreignKeys = [ForeignKey(
    entity = Contact::class,
    parentColumns = ["contactId"],
    childColumns = ["contactOwnerId"],
    onDelete = ForeignKey.CASCADE
)])
data class CallReminder (@PrimaryKey(autoGenerate = true) val id: Int, var dayTime: LocalDateTime, val contactOwnerId: Int): Parcelable