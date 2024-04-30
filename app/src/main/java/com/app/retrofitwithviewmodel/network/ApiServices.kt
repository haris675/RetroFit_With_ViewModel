package com.app.rescuemanagmentsystem.network

import com.app.retrofitwithviewmodel.utils.Constants
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {

    @GET(Constants.LATEST_NEWS)
     fun getNew(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Call<JsonElement>


}