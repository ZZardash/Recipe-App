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
import DatabaseHelper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.recipe.model.Category
import com.example.recipe.model.Recipe


class OvenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper

    private lateinit var timePicker: TimePicker
    private lateinit var startButton: Button
    private lateinit var temperature: EditText
    private lateinit var recipeList: MutableList<Recipe>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oven)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)

        recipeList = mutableListOf()
        temperature = findViewById(R.id.etTemperature)
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true) // 24 saat formatında göstermek için
        removeTimePickerAMPM() // AM/PM kısmını kaldırmak için özel bir işlem yapar

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {

            saveAllData()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            // Seçilen saat ve dakikayı kullanarak istediğiniz işlemi yapabilirsiniz
            // Örneğin, bir zamanlayıcı başlatma işlemi veya başka bir işlem gerçekleştirme

        }
    }

    private fun saveAllData() {

        val databaseHelper = DatabaseHelper(this)
        val temperatureUnit = getTemperatureUnit()
        val temp = temperature.text.toString()
        val temperature = "$temp °$temperatureUnit"

        val selectedHour = timePicker.hour.toString()
        val selectedMinute = timePicker.minute.toString()
        val selectedTime = "$selectedHour:$selectedMinute"

        sharedPreferences.saveData("Temperature", temperature)
        sharedPreferences.saveData("Selected Time", selectedTime)

        val recipeName = sharedPreferences.loadData("RecipeName")
        val categoryName = sharedPreferences.loadData("SelectedCategory")
        val ingredients = sharedPreferences.loadData("Ingredients")
        val ins = sharedPreferences.loadData("Instructions")
        val _temp = sharedPreferences.loadData("Temperature")
        val _time = sharedPreferences.loadData("Selected Time")
        val recipePhotoPath = sharedPreferences.loadData("RecipePhotoPath")
        val bitmapPhoto = decodeBitmapFromFile(recipePhotoPath)

        println(recipeName + "\n" + categoryName + "\n" + ingredients + "\n" + ins + "\n" + _temp + "\n" + _time + "\n" + recipePhotoPath)

        val recipeId = databaseHelper.insertRecipe(recipeName, categoryName, ingredients, ins, _temp, _time, recipePhotoPath)
        val recipe = Recipe(recipeId, recipeName, categoryName, ingredients, ins, _temp, _time, bitmapPhoto)
        recipeList.add(recipe)

    }

    private fun decodeBitmapFromFile(photoPath: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(photoPath, options)
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
