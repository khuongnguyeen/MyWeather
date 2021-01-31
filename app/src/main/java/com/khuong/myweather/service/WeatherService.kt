package com.khuong.myweather.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.khuong.myweather.application.MyApplication
import com.khuong.myweather.model.ListWeather
import com.khuong.myweather.model.WeatherData
import com.khuong.myweather.model.WeatherDataTwo

class WeatherService: LifecycleService() {
    private var weatherData:WeatherData?=null
    private var listWeather:ListWeather?=null
    private var listWer = mutableListOf<WeatherDataTwo>()

    fun getWeatherData() = weatherData
    fun getListWer() = listWer

    override fun onCreate() {
        super.onCreate()
        MyApplication.getWeather().weatherData.observe(this, {
            weatherData = it
        })
        MyApplication.getWeather().listWeather.observe(this, {
            listWeather = it
            listWer.clear()
            listWer.addAll(it.list)
        })
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return MyBinder(this)
    }

    class MyBinder(val service: WeatherService) : Binder()

}