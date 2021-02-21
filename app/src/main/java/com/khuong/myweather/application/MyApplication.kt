package com.khuong.myweather.application

import android.app.Application
import com.khuong.myweather.viewmodel.WeatherViewModel

class MyApplication: Application() {

    companion object{
        private lateinit var weatherViewModel: WeatherViewModel
        fun getWeather() = weatherViewModel
        var SETTING = 1
    }

    override fun onCreate() {
        super.onCreate()
        weatherViewModel = WeatherViewModel()
    }
}