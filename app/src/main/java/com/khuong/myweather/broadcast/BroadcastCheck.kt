package com.khuong.myweather.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.khuong.myweather.application.MyApplication

@Suppress("DEPRECATION")
class BroadcastCheck() : BroadcastReceiver() {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var s: String = ""
    fun setLo(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun setName(name: String) {
        this.s = name
    }



    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                if (isNetworksAvailable(context)) {
                    if (latitude != 0.0 && s == "") {
                        MyApplication.getWeather().getWeatherLocation(latitude, longitude)
                        MyApplication.getWeather().getWeekLocation(latitude, longitude)
                        Log.d(
                            "Debug:",
                            "------------------------------------------------->$latitude $longitude "
                        )
                    }
                    if (s != ""){
                        MyApplication.getWeather().getWeather(s)
                        MyApplication.getWeather().getWeek(s)
                        Log.d("Debug:", "-------------------sssssssssssssssssss--------------->$s")
                    }
                } else Toast.makeText(context, "Không có kết nối Internet", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun isNetworksAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    }
}