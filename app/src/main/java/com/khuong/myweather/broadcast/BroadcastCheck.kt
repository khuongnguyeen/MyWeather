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
import androidx.annotation.RequiresApi
import com.khuong.myweather.activity.PopUpWeather
import com.khuong.myweather.application.MyApplication

@Suppress("DEPRECATION")
open class BroadcastCheck() : BroadcastReceiver() {

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



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                if (isNetworksAvailable(context)) {
                    if (latitude != 0.0 && s == "") {
                        MyApplication.getWeather().getWeatherLocation(latitude, longitude)
                        MyApplication.getWeather().getWeekLocation(latitude, longitude)
                    }
                    if (s != ""){
                        MyApplication.getWeather().getWeather(s)
                        MyApplication.getWeather().getWeek(s)
                    }
                } else {
                    Toast.makeText(context, "Không có kết nối Internet", Toast.LENGTH_LONG).show()
                }
            }

//            Intent.ACTION_SCREEN_ON->{
//                Log.d("duykhuong","khuong ON")
//                Toast.makeText(context, "Screen ON", Toast.LENGTH_LONG).show()
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                PopUpWeather(context).show()
////                PopUpWeather(context).window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
//
//
//            }
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