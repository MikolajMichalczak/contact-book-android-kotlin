package com.example.contactbook.database.daos

import androidx.paging.DataSource
import androidx.room.*
import com.example.contactbook.database.entities.Repository

@Dao
interface  RepositoriesDao {

    @Query("SELECT * from repositories_table ORDER BY name DESC")
    fun getPagedRepos(): DataSource.Factory<Int,Repository>

    @Query("SELECT * FROM repositories_table where name LIKE :name ORDER BY name DESC")
    fun getSearchedRepos(name: String): DataSource.Factory<Int, Repository>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repository: Repository)

    @Query("DELETE FROM repositories_table")
    suspend fun deleteAllRepos()

    @Delete
    fun deleteRepo(repository: Repository)

    @Update
    fun updateRepo(repository: Repository)
}