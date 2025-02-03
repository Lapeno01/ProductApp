package com.example.model.api

import com.example.model.entities.ProductResponse
import retrofit2.http.GET

interface ProductApi {
    @GET("products-test.json")
    suspend fun getProducts(): ProductResponse
}