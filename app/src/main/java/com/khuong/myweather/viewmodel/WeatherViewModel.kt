package com.khuong.myweather.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khuong.myweather.api.ApiService
import com.khuong.myweather.api.RetrofitUtils
import com.khuong.myweather.model.ListWeather
import com.khuong.myweather.model.WeatherData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WeatherViewModel : ViewModel() {

    private val weatherAPI: ApiService = RetrofitUtils.createRetrofit()
    val weatherData = MutableLiveData<WeatherData>()
     val listWeather= MutableLiveData<ListWeather>()





    @SuppressLint("CheckResult")
    fun getWeather(
        q: String,
        lang: String = "vi",
        units: String = "metric",
        APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ) {
        weatherAPI.getWeather(q, lang, units, APPID)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    weatherData.value = it
//                    update(it)
                },
                {
                    Log.e("duy khuong", "----------------->>>> API <<<<-----------------")
                }
            )
    }

    @SuppressLint("CheckResult")
    fun getWeek(
        q: String,
        lang: String = "vi",
        units: String = "metric",
        cnt: Int = 16,
        APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ) {
        weatherAPI.getWeek(q, lang, units, cnt, APPID)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    listWeather.value = it
                    Log.d("duy khuong", "----------------->>>> API <<<<-----------------")
                },
                {
                    Log.e("duy khuong", "----------------->>>> API <<<<-----------------")
                }
            )
    }

    @SuppressLint("CheckResult")
    fun getWeekLocation(
        lat: Double,
        lon: Double,
        lang: String = "vi",
        units: String = "metric",
        cnt: Int = 16,
        APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ) {
        weatherAPI.getWeekLocation(lat, lon, lang, units, cnt, APPID)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    listWeather.value = it
                },
                {
                    Log.e("duy khuong", "----------------->>>> API <<<<-----------------")
                }
            )
    }

    @SuppressLint("CheckResult")
    fun getWeatherLocation(
        lat: Double,
        lon: Double,
        lang: String = "vi",
        units: String = "metric",
        APPID: String = "b0dc6be1752cbdafe881ea2a413811e4"
    ) {
        weatherAPI.getWeatherLocation(lat, lon, lang, units, APPID)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    weatherData.value = it
//                    update(it)
                },
                {
                    Log.e("duy khuong", "----------------->>>> API <<<<-----------------")
                }
            )
    }

}