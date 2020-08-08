package com.zafar.covid19tracking.services

import com.zafar.covid19tracking.model.CountryInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface DataService {
    @GET("data.json")
    fun getJsonData(): Call<List<CountryInfo>>

    @GET("latest.csv")
    fun downloadFileWithFixedUrl(): Call<ResponseBody?>?

}