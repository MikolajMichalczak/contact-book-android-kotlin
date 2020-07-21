package com.example.contactbook.database.daos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.contactbook.data.ReposDataSource
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.database.entities.Repository
import java.time.Period

@Dao
interface  RepositoriesDao {

    @Query("SELECT * from repositories_table ORDER BY name DESC")
    fun getPagedRepos(): DataSource.Factory<Int,Repository>

    @Query("SELECT * from repositories_table ORDER BY name DESC")
    fun getAllRepos(): LiveData<List<Repository>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repository: Repository)

    @Query("DELETE FROM repositories_table")
    suspend fun deleteAllRepos()

    @Delete
    fun deleteRepo(repository: Repository)

    @Update
    fun updateRepo(repository: Repository)
}