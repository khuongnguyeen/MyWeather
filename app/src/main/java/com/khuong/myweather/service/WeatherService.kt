package com.khuong.myweather.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
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
    private var popUpWeather : PopUpWeather? = null
    private lateinit var broadcast: BroadcastReceiver

    fun getWeatherData() = weatherData
    fun getListWer() = listWer

    override fun onCreate() {
        super.onCreate()
        popUpWeather = PopUpWeather(applicationContext)
        createNotification()
        Log.d("duykhuong", "Service onCreate-..............")

        MyApplication.getWeather().weatherData.observe(this, androidx.lifecycle.Observer {
            weatherData = it
        })
        MyApplication.getWeather().listWeather.observe(this, androidx.lifecycle.Observer {
            listWeather = it
        })
        MyApplication.getWeather().listWe.observe(this, androidx.lifecycle.Observer {
            listWer.clear()
            listWer.addAll(it)
        })

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        broadcast = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON->{

                        Log.d("duykhuong","khuong ON")
                        Toast.makeText(context, "Screen ON", Toast.LENGTH_LONG).show()
                        popUpWeather!!.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                        popUpWeather!!.show();
                    }
                }
            }
        }
        registerReceiver(broadcast, intentFilter)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
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
        Log.d("duykhuong", "Service onDestroy-..............")
    }

    private fun createNotification() {
        createChannel()
        val no = NotificationCompat.Builder(this,"NO")
            .setSmallIcon(R.drawable.w_1)
            .build()
        startForeground(1, no)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("NO", "NO", importance)
            channel.description = "NO"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

}