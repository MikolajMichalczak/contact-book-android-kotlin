package com.example.contactbook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.contactbook.database.daos.ContactsDao
import com.example.contactbook.database.daos.ContactsExtrasDao
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.entities.ContactExtras
import kotlinx.coroutines.CoroutineScope


@Database(entities = arrayOf(Contact::class, ContactExtras::class), version = 1, exportSchema = false)
public abstract class ContactsRoomDatabase : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao
    abstract fun contactsExtrasDao(): ContactsExtrasDao

//    private class ContactsDatabaseCallback(
//        private val scope: CoroutineScope
//    ) : RoomDatabase.Callback() {
//
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    var contactsExtrasDao = database.contactsExtrasDao()
//
//                    // Delete all content here.
//                    contactsExtrasDao.deleteAllExtras()
//
//                    // Add sample words.
//                    var contactExtras = ContactExtras(0, "mm@wp.pl", "Lea 21/2")
//                    contactsExtrasDao.insertExtras(contactExtras)
//                }
//            }
//        }
//    }


    companion object {

        @Volatile
        private var INSTANCE: ContactsRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ContactsRoomDatabase {

//            val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//                override fun migrate(database: SupportSQLiteDatabase) {
//                    database.execSQL("CREATE TABLE contacts_extras_table (id INTEGER NOT NULL, email TEXT NOT NULL, address TEXT NOT NULL, PRIMARY KEY(id))");
//                }
//            }

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactsRoomDatabase::class.java,
                    "contacts_database"
                )
                    //.addCallback(ContactsDatabaseCallback(scope))
                    //.addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        }
    }

