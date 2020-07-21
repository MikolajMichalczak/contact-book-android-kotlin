package com.example.contactbook.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories_table")
data class Repository (@PrimaryKey val id: String, val name: String, val language: String?, val description: String?)