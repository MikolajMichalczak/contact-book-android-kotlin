package com.example.contactbook.network

import com.example.contactbook.database.entities.Repository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://api.github.com"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface RepoApiService {
    @GET("/orgs/google/repos")
    suspend fun fetchRepos(@Query("page") page: Int,
                           @Query("per_page") perPage: Int = 15): List<Repository>
}

object RepoApi {
    val retrofitService : RepoApiService by lazy {
        retrofit.create(RepoApiService::class.java) }
}
