package com.khuong.myweather.service

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.khuong.myweather.application.MyApplication
import com.khuong.myweather.model.ListWeather
import com.khuong.myweather.model.WeatherData
import com.khuong.myweather.model.WeatherDataTwo


class WeatherService: LifecycleService() {
    private var weatherData:WeatherData?=null
    private var listWer = mutableListOf<WeatherDataTwo>()
    private var listWeather:ListWeather? = null
    private var broadCastReceiver: BroadcastReceiver? =null

    fun getWeatherData() = weatherData
    fun getListWer() = listWer

    override fun onCreate() {
        super.onCreate()
        MyApplication.getWeather().weatherData.observe(this, androidx.lifecycle.Observer{
            weatherData = it
        })
        MyApplication.getWeather().listWeather.observe(this, androidx.lifecycle.Observer{
            listWeather = it
        })
        MyApplication.getWeather().listWe.observe(this, androidx.lifecycle.Observer{
            listWer.clear()
            listWer.addAll(it)
        })

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(broadCastReceiver, intentFilter)


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return  START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return MyBinder(this)
    }

    class MyBinder(val service: WeatherService) : Binder()

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }


}