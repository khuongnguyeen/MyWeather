package com.khuong.myweather.activity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatDialog
import androidx.core.graphics.ColorUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.khuong.myweather.application.MyApplication
import com.khuong.myweather.databinding.PopupBinding
import com.khuong.myweather.model.ListWeather
import com.khuong.myweather.model.WeatherData
import com.khuong.myweather.service.WeatherService
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class PopUpWeather(context: Context) : Dialog(context) {

    private lateinit var binding: PopupBinding
    private var service: WeatherService? = null
    private var conn: ServiceConnection? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    fun setLo(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = PopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataLocal()


        binding.border.alpha = 0f
        binding.border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()
        binding.background.setOnClickListener {
            dismiss()
        }

    }

    private fun getDataLocal() {
        val sharedPreferences: SharedPreferences =
            context.applicationContext.getSharedPreferences("weatherSetting", Context.MODE_PRIVATE)
        val string = sharedPreferences.getString("weather", "")
        val g = Gson()
        if (g.fromJson(string, WeatherData::class.java) != null) {
            update(g.fromJson(string, WeatherData::class.java))

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        dismiss()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun update(weatherData: WeatherData) {
        val date = SimpleDateFormat(" HH:mm:ss _ dd/MM/yyyy")
            .format(Date(weatherData.dt * 1000L))
        val sunrise = SimpleDateFormat(" HH:mm")
            .format(Date(weatherData.sys.sunrise * 1000L))
        val sunset = SimpleDateFormat(" HH:mm")
            .format(Date(weatherData.sys.sunset * 1000L))
        Glide.with(context)
            .load("http://openweathermap.org/img/wn/${weatherData.weather[0].icon}@2x.png")
            .into(binding.ivWeather)
        binding.tvCity.text = weatherData.name + ", " + weatherData.sys.country
        binding.tvWeatherOne.text = capitalizeString(weatherData.weather[0].description)
        binding.tvWeather.text = "${weatherData.main.temp.toInt()}°"
        binding.tvTocDoGio.text = weatherData.wind.speed.toString()
        binding.tvDoAm.text = weatherData.main.humidity.toString() + "%"
        binding.tvSunrise.text = sunrise
        binding.tvSunset.text = sunset
        binding.tvTime.text = "Cập nhật lần cuối: $date"
    }

    private fun capitalizeString(string: String): String {
        val chars = string.toLowerCase(Locale.ROOT).toCharArray()
        var found = false
        for (i in chars.indices) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i])
                found = true
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false
            }
        }
        return String(chars)
    }


}