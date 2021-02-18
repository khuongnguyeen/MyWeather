package com.khuong.myweather.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.khuong.myweather.R
import com.khuong.myweather.broadcast.BroadcastCheck
import com.khuong.myweather.databinding.ActivityFragmentBinding
import com.khuong.myweather.fragment.FaceFragment
import com.khuong.myweather.fragment.WeatherFragment


@Suppress("DEPRECATION")
class FragmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var broadcastCheck: BroadcastCheck

    companion object {
        const val PERMISSION_ID = 1010
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fragment)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        broadcastCheck = BroadcastCheck()
        requestPermission()
        getLastLocation()
        sttBar()
    }

    private fun sttBar() {
        if (Build.VERSION.SDK_INT in 19..20) WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.setWindowFlag(
            this,
            true
        )
        if (Build.VERSION.SDK_INT >= 19) window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= 21) {
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.setWindowFlag(this, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun Int.setWindowFlag(activity: Activity, on: Boolean) {
        val win: Window = activity.window
        val winParams: WindowManager.LayoutParams = win.attributes
        if (on) winParams.flags = winParams.flags or this else winParams.flags =
            winParams.flags and inv()
        win.attributes = winParams
    }

    private fun addWeatherFragment(lat: Double, long: Double) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        val fragment = WeatherFragment(lat, long)
        transaction.replace(R.id.content, fragment)
            .commit()
    }

    private fun addFaceFragment() {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        val fragment = FaceFragment()
        transaction.replace(R.id.content, fragment)
            .commit()
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        newLocationData()
                    } else {
                        addWeatherFragment(location.latitude, location.longitude)
                        broadcastCheck.setLo(location.latitude, location.longitude)
                        PopUpWeather(applicationContext).setLo(location.latitude, location.longitude)
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Vui lòng bật vị trí trên thiết bị của bạn",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            requestPermission()
            addFaceFragment()
        }
    }

    private fun newLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            addWeatherFragment(lastLocation.latitude, lastLocation.longitude)
        }
    }

    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    override fun onStart() {
        getLastLocation()
        super.onStart()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(broadcastCheck, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastCheck)
    }
}