package com.khuong.myweather.fragment

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.khuong.myweather.R
import com.khuong.myweather.activity.PopUpWeather
import com.khuong.myweather.adapter.WeatherAdapter
import com.khuong.myweather.application.MyApplication
import com.khuong.myweather.broadcast.BroadcastCheck
import com.khuong.myweather.databinding.FragmentWeatherBinding
import com.khuong.myweather.model.ListWeather
import com.khuong.myweather.model.WeatherData
import com.khuong.myweather.model.WeatherDataTwo
import com.khuong.myweather.service.WeatherService
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class WeatherFragment(private val latitude: Double, private val longitude: Double) : Fragment(),
    SwipeRefreshLayout.OnRefreshListener, WeatherAdapter.IWeather {

    private lateinit var binding: FragmentWeatherBinding
    private var service: WeatherService? = null
    private var conn: ServiceConnection? = null
    private var s = ""
    private lateinit var broadcastCheck: BroadcastCheck
    private var weatherData: WeatherData? = null
    var listWeather: ListWeather? = null
    private val listWe = mutableListOf<WeatherDataTwo>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        if (!isNetworksAvailable(context!!.applicationContext)) {
            binding.rl.visibility = View.VISIBLE
        }
        getDataLocal()
        broadcastCheck = BroadcastCheck()
        sync()
        binding.rc.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rc.adapter = WeatherAdapter(this)
        openServiceUnBound()
        createConnectService()
        register()
        binding.btnSearch.setOnClickListener {
            binding.layoutSearch.visibility = View.VISIBLE
            binding.btnSearch.setImageResource(R.drawable.search_off)
            binding.edtSearch.text = null
            binding.tvCity.visibility = View.GONE
        }
        binding.ivWeather.setOnClickListener {
            PopUpWeather(context!!).show()
        }
        binding.edtSearch.setOnEditorActionListener { _, i, _ ->
            return@setOnEditorActionListener when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    if (binding.edtSearch.text.toString() == "") {
                        MyApplication.getWeather().getWeatherLocation(latitude, longitude)
                        MyApplication.getWeather().getWeekLocation(latitude, longitude)
                    } else {
                        MyApplication.getWeather().getWeather(binding.edtSearch.text.toString())
                        MyApplication.getWeather().getWeek(binding.edtSearch.text.toString())
                    }
                    binding.layoutSearch.visibility = View.GONE
                    binding.tvCity.visibility = View.VISIBLE
                    binding.btnSearch.setImageResource(R.drawable.search)
                    val imm =
                        context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
                    binding.rc.adapter!!.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        return binding.root
    }

    private fun register() {

        MyApplication.getWeather().weatherData.observe(this, androidx.lifecycle.Observer{
            update(it)
            s = it.name
            weatherData = it
            broadcastCheck.setName(s)
        })
        MyApplication.getWeather().listWeather.observe(this,  androidx.lifecycle.Observer{
            listWeather = it
        })
        MyApplication.getWeather().listWe.observe(this,  androidx.lifecycle.Observer{
            binding.rc.adapter!!.notifyDataSetChanged()
        })
        if (weatherData != null && listWeather != null) {
            setDataLocal(weatherData!!, listWeather!!)
            listWe.addAll(listWeather!!.list)
        }
        getDataLocal()

    }

    private fun openServiceUnBound(){
        val intent = Intent()
        intent.setClass(context!!, WeatherService::class.java)
        context!!.startService(intent)
    }

    private fun createConnectService() {
        conn = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
                val myBinder = binder as WeatherService.MyBinder
                service = myBinder.service
                if (service!!.getListWer().size == 0) MyApplication.getWeather().getWeekLocation(
                    latitude,
                    longitude
                ) else binding.rc.adapter!!.notifyDataSetChanged()
                if (service!!.getWeatherData() == null) MyApplication.getWeather()
                    .getWeatherLocation(latitude, longitude)
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
        Handler().postDelayed({ binding.swipeRefreshLayout.isRefreshing = false }, 3000)
        MyApplication.getWeather().getWeek(s)
        MyApplication.getWeather().getWeather(s)
        register()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun update(weatherData: WeatherData) {
        binding.rl.visibility = View.GONE
        binding.rl2.visibility = View.GONE
        val date = SimpleDateFormat(" HH:mm:ss _ dd/MM/yyyy")
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

    override fun getCount(): Int {

        return if (!isNetworksAvailable(context!!.applicationContext) && listWeather != null) {
            listWeather!!.list.size
        } else {
            if (service == null) {
                return 0
            }
            return service!!.getListWer().size
        }
    }


    override fun getData(position: Int): WeatherDataTwo {

        return if (!isNetworksAvailable(context!!.applicationContext) && listWeather != null) {
            listWeather!!.list[position]
        } else
            service!!.getListWer()[position]
    }

    fun sync() {
        val async = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                for (i in 1..300) {
                    Log.d("Debug:", "------------------------------------------------->$i")
                    SystemClock.sleep(1000)
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                register()
                MyApplication.getWeather().getWeather(s)
                MyApplication.getWeather().getWeek(s)
//                register()
                Log.d("Debug:", "------=========================-----------------------> ")
                sync()
            }
        }
        async.execute()
    }

    private fun getDataLocal() {
        val sharedPreferences: SharedPreferences =
            context!!.applicationContext.getSharedPreferences("weatherSetting", Context.MODE_PRIVATE)
        val string = sharedPreferences.getString("weather", "")
        val string2 = sharedPreferences.getString("listWea", "")
        val g = Gson()
        if (g.fromJson(string, WeatherData::class.java) != null) {
            update(g.fromJson(string, WeatherData::class.java))
            listWeather = g.fromJson(string2, ListWeather::class.java)
            binding.rl.visibility = View.GONE
            Log.d("khuongkk:", "Lưu thành công")
        }
    }

    private fun setDataLocal(weatherData: WeatherData, listWeather: ListWeather) {
        val sharedPreferences: SharedPreferences =
            context!!.applicationContext.getSharedPreferences(
                "weatherSetting",
                Context.MODE_PRIVATE
            )
        val editor = sharedPreferences.edit()
        val g = Gson()
        val string2 = g.toJson(listWeather)
        editor.putString("listWea", string2)
        val string = g.toJson(weatherData)
        editor.putString("weather", string)
        editor.apply()
        Log.d("khuongkk:", "Lưu thành công")
    }

}




