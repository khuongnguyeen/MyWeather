package com.khuong.myweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khuong.myweather.databinding.ItemWeatherBinding
import com.khuong.myweather.model.WeatherDataTwo
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter(private val inter: IWeather) : RecyclerView.Adapter<WeatherAdapter.WeatherHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        return WeatherHolder(
            ItemWeatherBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
        val date = SimpleDateFormat("HH:mm\ndd/MM")
            .format(Date(inter.getData(position).dt * 1000L))
        Glide.with(holder.binding.root.context)
            .load("http://openweathermap.org/img/wn/${inter.getData(position).weather[0].icon}@2x.png")
            .into(holder.binding.ivWeatherTwo)
        holder.binding.tvThu.text = date
        holder.binding.tvDoMax.text = "${inter.getData(position).main.temp_max.toInt()}°"
    }

    override fun getItemCount() = inter.getCount()

    interface IWeather {
        fun getCount(): Int
        fun getData(position:Int): WeatherDataTwo
    }


    class WeatherHolder(val binding: ItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)
}