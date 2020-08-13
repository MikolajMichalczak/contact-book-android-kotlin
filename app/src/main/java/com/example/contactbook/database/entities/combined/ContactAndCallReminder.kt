package com.example.contactbook.database.entities.combined

import androidx.room.Embedded
import androidx.room.Relation
import com.example.contactbook.database.entities.CallReminder
import com.example.contactbook.database.entities.Contact

data class ContactAndCallReminder(
    @Embedded val contact: Contact,
    @Relation(
        parentColumn = "contactId",
        entityColumn = "contactOwnerId"
    )
    val callReminder: CallReminder?
)