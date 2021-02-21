package com.khuong.myweather.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.khuong.myweather.R
import com.khuong.myweather.activity.PopUpWeather
import com.khuong.myweather.application.MyApplication
import com.khuong.myweather.model.ListWeather
import com.khuong.myweather.model.WeatherData
import com.khuong.myweather.model.WeatherDataTwo


@Suppress("DEPRECATION")
class WeatherService : LifecycleService() {
    private var weatherData: WeatherData? = null
    private var listWer = mutableListOf<WeatherDataTwo>()
    private var listWeather: ListWeather? = null
    private var popUpWeather: PopUpWeather? = null
    private var no: Notification? = null
    private lateinit var broadcast: BroadcastReceiver
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

    fun getWeatherData() = weatherData
    fun getListWer() = listWer

    override fun onCreate() {
        super.onCreate()
        createNotification()
        Log.d("duykhuong", "Service onCreate-..............")

        MyApplication.getWeather().weatherData.observe(this, androidx.lifecycle.Observer {
            weatherData = it
            popUpWeather = PopUpWeather(applicationContext,it)
        })
        MyApplication.getWeather().listWeather.observe(this, androidx.lifecycle.Observer {
            listWeather = it
        })
        MyApplication.getWeather().listWe.observe(this, androidx.lifecycle.Observer {
            listWer.clear()
            listWer.addAll(it)
        })


        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)

        broadcast = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ConnectivityManager.CONNECTIVITY_ACTION -> {
                        Log.d("duykhuong", "khuong ON    ConnectivityManager.CONNECTIVITY_ACTION")
                        if (isNetworksAvailable(applicationContext)) {
                            if (latitude != 0.0 && s == "") {
                                MyApplication.getWeather().getWeatherLocation(latitude, longitude)
                                MyApplication.getWeather().getWeekLocation(latitude, longitude)
                            }
                            if (s != "") {
                                MyApplication.getWeather().getWeather(s)
                                MyApplication.getWeather().getWeek(s)
                            }
                        } else {
                            Toast.makeText(context, "Không có kết nối Internet", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        if (MyApplication.SETTING == 1) {
                            popUpWeather!!.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                            popUpWeather!!.show()
                        }
                    }
                }
            }
        }
        registerReceiver(broadcast, intentFilter)

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (MyApplication.SETTING == 2 && intent!!.getIntExtra("setting", 0) == 1) {
            popUpWeather!!.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            popUpWeather!!.show()
        }
        Log.d("duykhuong", "Service onStartCommand-..............")
        return START_STICKY
    }


    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d("duykhuong", "Service onStart-..............")
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return MyBinder(this)
    }

    class MyBinder(val service: WeatherService) : Binder()

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcast)
    }

    private fun createNotification() {
        createChannel()
        no = NotificationCompat.Builder(this, "NO")
            .setSmallIcon(R.drawable.w_1)
            .setPriority(Notification.PRIORITY_MIN)
            .build()
        startForeground(1, no)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("NO", "Nguyễn Duy Khương", importance)
            channel.description = "NO"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

}