package com.khuong.myweather.application

import android.app.Application
import com.khuong.myweather.viewmodel.WeatherViewModel

class MyApplication: Application() {

    lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate() {
        super.onCreate()
        weatherViewModel = WeatherViewModel()
    }
}