package com.khuong.myweather.dialog

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import com.google.gson.Gson
import com.khuong.myweather.application.MyApplication
import com.khuong.myweather.broadcast.BroadcastCheck
import com.khuong.myweather.databinding.SettingBinding
import com.khuong.myweather.model.WeatherData
import java.util.*


@Suppress("DEPRECATION")
class DialogSetting(context: Context) : Dialog(context) {

    private lateinit var binding: SettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = SettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendar = Calendar.getInstance()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BroadcastCheck::class.java)
        var setting: Int = 1
        binding.radioOne.isChecked = true
        binding.timePicker.setIs24HourView(true)
        binding.radioOne.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> // TODO Auto-generated method stub
            if (buttonView.isChecked) {
                setting = 1
                Log.i("duykhuong", "RadioButton " + MyApplication.SETTING + " : " + isChecked)
            }
        })
        binding.radioTwo.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> // TODO Auto-generated method stub
            if (buttonView.isChecked) {
                binding.timePicker.visibility = View.VISIBLE
                setting = 2
                Log.i("duykhuong", "RadioButton " + MyApplication.SETTING + " : " + isChecked)
            } else {
                binding.timePicker.visibility = View.GONE
            }
        })

        binding.btnDone.setOnClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, binding.timePicker.currentHour)
            calendar.set(Calendar.MINUTE, binding.timePicker.currentMinute)

            val gio = binding.timePicker.currentHour
            val phut = binding.timePicker.currentMinute

            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            dismiss()
            MyApplication.SETTING = setting
            setDataLocal(setting)
            binding.text.text = "Bạn vừa chọn: $gio : $phut"
        }
    }



    private fun setDataLocal(i: Int) {
        val sharedPreferences: SharedPreferences =
            context.applicationContext.getSharedPreferences(
                "setting",
                Context.MODE_PRIVATE
            )
        val editor = sharedPreferences.edit()
        editor.putInt("myappsetting", i)
        editor.apply()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        dismiss()
    }

}