package com.example.rechic.repository

import com.example.rechic.database.remote.retrofit.CountryApi
import com.example.rechic.model.Country

class CountryRepository(private val api: CountryApi) {
    suspend fun getCountries(): List<Country> = api.getCountries()
}