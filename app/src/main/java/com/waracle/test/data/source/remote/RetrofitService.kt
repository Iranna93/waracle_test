package com.waracle.test.data.source.remote

import com.waracle.test.data.entities.CakeEntity
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitService {
    @GET
    suspend fun getCakesList(@Url url: String): List<CakeEntity>
}
