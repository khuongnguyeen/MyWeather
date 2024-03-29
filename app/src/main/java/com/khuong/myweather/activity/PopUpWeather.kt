package com.khuong.myweather.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.view.Window
import android.view.animation.DecelerateInterpolator
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.khuong.myweather.databinding.PopupBinding
import com.khuong.myweather.model.WeatherData
import com.khuong.myweather.service.WeatherService
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class PopUpWeather(context: Context,val weatherData:WeatherData) : Dialog(context) {

    private lateinit var binding: PopupBinding
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
        update(weatherData)


        binding.border.alpha = 0f
        binding.border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()
        binding.background.setOnClickListener {
            dismiss()
            val intent = Intent(context, WeatherService::class.java)
            context.stopService(intent)
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        dismiss()
        val intent = Intent(context, WeatherService::class.java)
        context.stopService(intent)
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