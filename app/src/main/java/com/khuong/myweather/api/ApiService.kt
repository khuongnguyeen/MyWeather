package com.khuong.myweather.api

import com.khuong.myweather.model.ListWeather
import com.khuong.myweather.model.WeatherData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
//    /data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
    @GET("/data/2.5/weather")
    fun getWeather(
        @Query("q") q: String,
        @Query("lang") lang: String = "vi",
        @Query("units") units: String = "metric",
        @Query("APPID") APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ): Observable<WeatherData>

    @GET("/data/2.5/weather")
    fun getWeatherLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String = "vi",
        @Query("units") units: String = "metric",
        @Query("APPID") APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ): Observable<WeatherData>

    @GET("/data/2.5/forecast")
    fun getWeekLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String = "vi",
        @Query("units") units: String = "metric",
        @Query("cnt") cnt: Int = 16,
        @Query("APPID") APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ): Observable<ListWeather>

    @GET("/data/2.5/forecast")
    fun getWeek(
        @Query("q") q: String,
        @Query("lang") lang: String = "vi",
        @Query("units") units: String = "metric",
        @Query("cnt") cnt: Int = 16,
        @Query("APPID") APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ): Observable<ListWeather>
}
