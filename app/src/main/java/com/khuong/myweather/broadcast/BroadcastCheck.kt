package com.khuong.myweather.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.widget.Toast

class BroadcastCheck : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            ConnectivityManager.CONNECTIVITY_ACTION->{
                if(isNetworksAvailable(context)) {}
                else Toast.makeText(context,"Internet Disconnected",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun isNetworksAvailable(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager == null) return false

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork
            if (network == null) return false
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }else{
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    }
}