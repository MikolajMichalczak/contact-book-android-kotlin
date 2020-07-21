package com.example.contactbook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.contactbook.database.daos.ContactsDao
import com.example.contactbook.database.daos.ContactsExtrasDao
import com.example.contactbook.database.daos.RepositoriesDao
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.database.entities.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [Contact::class, ContactExtras::class, Repository::class], version = 1, exportSchema = false)
public abstract class ContactsRoomDatabase : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao
    abstract fun contactsExtrasDao(): ContactsExtrasDao
    abstract  fun repositoriesDao(): RepositoriesDao

//    private class ContactsDatabaseCallback(
//        private val scope: CoroutineScope
//    ) : RoomDatabase.Callback() {
//
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    var reposDao = database.repositoriesDao()
//
//                    // Delete all content here.
//                    reposDao.deleteAllRepos()
//
//                    // Add sample words.
//                    var repo = Repository("42", " fgregh", "erry", "ert")
//                    var repo2 = Repository("43", " fgregh", "erry", "ert")
//                    var repo3 = Repository("44", " fgregh", "erry", "ert")
//                    reposDao.insertRepo(repo)
//                    reposDao.insertRepo(repo2)
//                    reposDao.insertRepo(repo3)
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
//                    database.execSQL("ALTER TABLE contacts_table "
//                            + " ADD COLUMN favourite INTEGER");
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

