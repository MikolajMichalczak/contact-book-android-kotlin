package com.example.contactbook.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "contacts_table")
data class Contact(@PrimaryKey(autoGenerate = true) val id: Int, var name: String, var number: String) : Parcelable