package com.example.recipe.activity

import DatabaseHelper
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.recipe.R
import com.example.recipe.model.Recipe
import com.example.recipe.util.SharedPreferencesHelper
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class OvenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var timePicker: TimePicker
    private lateinit var startButton: Button
    private lateinit var temperature: EditText
    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var btnCancelRecipe: Button
    private lateinit var lottieAnimationView: LottieAnimationView

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
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)

        lottieAnimationView = findViewById(R.id.lottie_save)
        lottieAnimationView.setAnimation(R.raw.saved)

        btnCancelRecipe.setOnClickListener{
            showCancelConfirmationDialog()
        }

        startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            lottieAnimationView.playAnimation()

            GlobalScope.launch {
                delay(2500L)
                saveAllData()
                sharedPreferences.deleteData("videoLink")
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                // Your delayed code here
            }
        }
    }

    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Kullanıcı "Yes" butonuna tıkladığında yapılacak işlemler
            // Örneğin, Main Activity'e dönüş işlemi
            sharedPreferences.deleteData("videoLink")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
            finish() // Bu aktiviteyi kapat
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // Kullanıcı "No" butonuna tıkladığında yapılacak işlemler
            dialog.dismiss() // Dialogu kapat
        }
        val alertDialog = alertDialogBuilder.create()

        // Ekranın ortasına kayma animasyonu eklemek için aşağıdaki satırı kullanabilirsiniz
        alertDialog.window?.attributes?.windowAnimations = 0
        alertDialog.show()
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
        val recipeRate = sharedPreferences.loadData("recipeRate")
        val videoLink = sharedPreferences.loadData("videoLink")

        println(ingredients)

        println("!!!"+videoLink+"!!!")

        println(recipeName + "\n" + categoryName + "\n" + ingredients + "\n" + ins + "\n" + _temp + "\n" + _time + "\n" + recipePhotoPath + "\n" + recipeRate+ "\n" + videoLink)

        val recipeId = databaseHelper.insertRecipe(recipeName, categoryName, ingredients, ins, _temp, _time, recipePhotoPath, recipeRate, videoLink)
        val recipe = Recipe(recipeId, recipeName, categoryName, ingredients, ins, _temp, _time, bitmapPhoto, recipeRate, videoLink)
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
