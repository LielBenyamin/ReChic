package com.example.rechic.database.remote.retrofit

import com.example.rechic.model.Country
import retrofit2.http.GET

interface CountryApi {
    @GET("v3.1/all?fields=idd,flags")
    suspend fun getCountries(): List<Country>
}