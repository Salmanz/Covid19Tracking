package com.zafar.covid19tracking.services

import CountryInfo
import retrofit2.Call
import retrofit2.http.GET

interface DataService {
    @GET("data.json")
    fun getJsonData(): Call<List<CountryInfo>>

}