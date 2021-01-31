package com.khuong.myweather.model

data class WeatherData(
    val weather: MutableList<Weather>,
    val main: WeatherMain,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val name: String
)