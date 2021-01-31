package com.khuong.myweather.fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.khuong.myweather.adapter.WeatherAdapter
import com.khuong.myweather.application.MyApplication
import com.khuong.myweather.databinding.FragmentWeatherBinding
import com.khuong.myweather.model.WeatherData
import com.khuong.myweather.model.WeatherDataTwo
import com.khuong.myweather.service.WeatherService
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment(private val latitude:Double, private val longitude:Double) : Fragment() , SwipeRefreshLayout.OnRefreshListener, WeatherAdapter.IWeather{

    private lateinit var binding: FragmentWeatherBinding
    private var service:WeatherService?=null
    private var conn : ServiceConnection?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        binding.rc.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rc.adapter = WeatherAdapter(this)
        createConnectService()
        register()
        binding.btnSearch.setOnClickListener {
            binding.layoutSearch.visibility = View.VISIBLE
            binding.edtSearch.text = null
            binding.tvCity.visibility = View.GONE
        }
        binding.edtSearch.setOnEditorActionListener { _, i, _ ->
            return@setOnEditorActionListener when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    if (binding.edtSearch.text.toString() == "") {
                        (context!!.applicationContext as MyApplication).weatherViewModel.getWeatherLocation(latitude,longitude)
                        (context!!.applicationContext as MyApplication).weatherViewModel.getWeekLocation(latitude,longitude)
                        binding.rc.adapter!!.notifyDataSetChanged()
                    } else {
                        (context!!.applicationContext as MyApplication).weatherViewModel.getWeather(binding.edtSearch.text.toString())
                        (context!!.applicationContext as MyApplication).weatherViewModel.getWeek(binding.edtSearch.text.toString())
                        binding.rc.adapter!!.notifyDataSetChanged()
                    }
                    binding.layoutSearch.visibility = View.GONE
                    binding.tvCity.visibility = View.VISIBLE
                    val imm =
                        context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
                    true
                }
                else -> false
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        return binding.root
    }

    private fun register(){
        (context!!.applicationContext as MyApplication).weatherViewModel.weatherData.observe(this, Observer {
            update(it)
        })


    }

    private fun createConnectService(){
        conn= object : ServiceConnection{
            override fun onServiceDisconnected(name: ComponentName?) {
            }
            override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
                val myBinder = binder as WeatherService.MyBinder
                service = myBinder.service
                if ( service!!.getListWer().size == 0) (context!!.applicationContext as MyApplication)
                    .weatherViewModel.getWeekLocation(latitude,longitude) else binding.rc.adapter!!.notifyDataSetChanged()

                if(service!!.getWeatherData()==null) (context!!.applicationContext as MyApplication).weatherViewModel
                    .getWeatherLocation(latitude,longitude)
            }
        }
        val intent = Intent()
        intent.setClass(context!!, WeatherService::class.java)
        context!!.bindService(intent, conn!!, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroyView() {
        context!!.unbindService(conn!!)
        super.onDestroyView()
    }

    override fun onRefresh() {
        val handler = Handler()
        handler.postDelayed({ binding.swipeRefreshLayout.isRefreshing = false },3000)
        if (binding.edtSearch.text.toString() == "") {
            (context!!.applicationContext as MyApplication).weatherViewModel.getWeatherLocation(latitude,longitude)
            (context!!.applicationContext as MyApplication).weatherViewModel.getWeekLocation(latitude,longitude)
        } else {
            (context!!.applicationContext as MyApplication).weatherViewModel.getWeather(binding.edtSearch.text.toString())
            (context!!.applicationContext as MyApplication).weatherViewModel.getWeek(binding.edtSearch.text.toString())
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun update(weatherData: WeatherData) {

        binding.rc.adapter!!.notifyDataSetChanged()
        val date = SimpleDateFormat(" HH:mm:ss dd/MM/yyyy")
            .format(Date(weatherData.dt * 1000L))
        val sunrise = SimpleDateFormat(" HH:mm")
            .format(Date(weatherData.sys.sunrise * 1000L))
        val sunset = SimpleDateFormat(" HH:mm")
            .format(Date(weatherData.sys.sunset * 1000L))
        Glide.with(this)
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

    override fun getCount(): Int {
        if ( service == null){
            return 0
        }
        return service!!.getListWer().size
    }

    override fun getData(position: Int): WeatherDataTwo
    {
        return service!!.getListWer()[position]
    }


}