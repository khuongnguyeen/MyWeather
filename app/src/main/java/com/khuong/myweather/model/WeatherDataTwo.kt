package com.khuong.myweather.model

data class WeatherDataTwo(
    val weather: MutableList<Weather>,
    val main: WeatherMain,
    val dt: Long,
    val dt_txt: String
)
