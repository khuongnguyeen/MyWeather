package com.khuong.myweather.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.khuong.myweather.service.WeatherService

@Suppress("NAME_SHADOWING")
open class BroadcastCheck : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val intent = Intent(context, WeatherService::class.java)
        intent.putExtra("setting",1)
        context.startService(intent)
    }

}

