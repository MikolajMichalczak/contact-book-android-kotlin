package com.example.contactbook.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "contacts_extras_table", foreignKeys = [ForeignKey(entity = Contact::class,
        parentColumns = arrayOf("id"),
    childColumns = arrayOf("contactOwnerId"),
    onDelete = CASCADE)]
)
data class ContactExtras(@PrimaryKey(autoGenerate = true) val id: Int, var email: String, var address: String, var contactOwnerId: Int) : Parcelable