package com.example.contactapp.data.remote.api

import com.example.contactapp.data.remote.dto.PicsumImageDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PicsumApi {


    @GET("id/{id}/info")
    suspend fun getImageInfo(@Path("id") id: Int): PicsumImageDto
}

