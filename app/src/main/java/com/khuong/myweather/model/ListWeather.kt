package com.khuong.myweather.model

data class ListWeather(
    val list:MutableList<WeatherDataTwo>,
    val city: City
)