package com.android.app.weather.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


// Example query :http://api.openweathermap.org/data/2.5/weather?zip=08853&appid=5ad7218f2e11df834b0eaf3a33a39d2a
interface WeatherApi {

    @GET("/data/2.5/weather")
    fun getWeatherInfo(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appid: String,
        @Query("mode") mode: String
    ): Call<WeatherServerResponse>

}