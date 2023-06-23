package com.example.recipe.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe.R
import com.example.recipe.util.SharedPreferencesHelper
import com.google.android.material.switchmaterial.SwitchMaterial

class OvenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper

    private lateinit var timePicker: TimePicker
    private lateinit var startButton: Button
    private lateinit var temperature: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oven)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)

        temperature = findViewById(R.id.etTemperature)
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true) // 24 saat formatında göstermek için
        removeTimePickerAMPM() // AM/PM kısmını kaldırmak için özel bir işlem yapar

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {

            saveAllData()
            // Seçilen saat ve dakikayı kullanarak istediğiniz işlemi yapabilirsiniz
            // Örneğin, bir zamanlayıcı başlatma işlemi veya başka bir işlem gerçekleştirme

        }
    }

    private fun saveAllData() {
        val temperatureUnit = getTemperatureUnit()
        val temp = temperature.text.toString()
        val Temperature = "$temp °$temperatureUnit"

        val selectedHour = timePicker.hour.toString()
        val selectedMinute = timePicker.minute.toString()
        val selectedTime = "$selectedHour:$selectedMinute"

        sharedPreferences.saveData("Temperature", Temperature)
        sharedPreferences.saveData("Selected Time", selectedTime)

        val recipeName = sharedPreferences.loadData("RecipeName")
        val categoryName = sharedPreferences.loadData("SelectedCategory")
        val ingredients = sharedPreferences.loadData("Ingredients")
        val ins = sharedPreferences.loadData("Instructions")
        val _temp = sharedPreferences.loadData("Temperature")
        val _time = sharedPreferences.loadData("Selected Time")

        println(recipeName + "\n" + categoryName + "\n" + ingredients + "\n" + ins + "\n" + _temp + "\n" + _time)
    }

    private fun getTemperatureUnit(): String {
        val switchTemperatureUnit = findViewById<SwitchMaterial>(R.id.switchTemperatureUnit)
        return if (switchTemperatureUnit.isChecked) {
            "F"
        } else {
            "C"
        }
    }

    // TimePicker'dan AM/PM kısmını kaldıran işlev
    private fun removeTimePickerAMPM() {
        val id = resources.getIdentifier("amPm", "id", "android")
        val amPmView = timePicker.findViewById<View>(id)
        if (amPmView != null) {
            (amPmView.parent as LinearLayout).removeView(amPmView)
        }
    }




}
