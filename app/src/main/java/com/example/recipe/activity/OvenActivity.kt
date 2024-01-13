// Fırın (Oven) Aktivite
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OvenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var cookingTimePicker: TimePicker
    private lateinit var preparationTimePicker: TimePicker
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

        cookingTimePicker = findViewById(R.id.cookingTimePicker)
        cookingTimePicker.setIs24HourView(true) // 24 saat formatında göstermek için

        preparationTimePicker = findViewById(R.id.preparationTimePicker)
        preparationTimePicker.setIs24HourView(true) // 24 saat formatında göstermek için

        removeTimePickerAMPM(cookingTimePicker) // AM/PM kısmını kaldırmak için özel bir işlem yapar
        removeTimePickerAMPM(preparationTimePicker) // AM/PM kısmını kaldırmak için özel bir işlem yapar

        startButton = findViewById(R.id.startButton)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)

        lottieAnimationView = findViewById(R.id.lottie_save)
        lottieAnimationView.setAnimation(R.raw.saved)

        btnCancelRecipe.setOnClickListener{
            showCancelConfirmationDialog()
        }

        startButton.setOnClickListener {
            // Disable the button to prevent multiple clicks during animation
            startButton.isEnabled = false

            val intent = Intent(this, MainActivity::class.java)
            lottieAnimationView.playAnimation()

            GlobalScope.launch(Dispatchers.Main) {
                delay(2500L)
                saveAllData()
                sharedPreferences.deleteData("videoLink")
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                // Re-enable the button after the animation is complete
                startButton.isEnabled = true
            }
        }

    }
    // İptal işlemini onaylama dialogunu göster
    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Kullanıcı "Yes" butonuna tıkladığında yapılacak işlemler
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

        // Ekranın ortasına kayma animasyonu eklemek için
        alertDialog.window?.attributes?.windowAnimations = 0
        alertDialog.show()
    }

    // Tüm verileri kaydet
    private fun saveAllData() {

        val databaseHelper = DatabaseHelper(this)
        val temperatureUnit = getTemperatureUnit()
        val temp = temperature.text.toString()
        val temperature = "$temp °$temperatureUnit"

        val cookingHour = cookingTimePicker.hour.toString()
        val cookingMinute = cookingTimePicker.minute.toString()
        val cookingTime = "$cookingHour:$cookingMinute"

        val preparationHour = preparationTimePicker.hour.toString()
        val preparationMinute = preparationTimePicker.minute.toString()
        val preparationTime = "$preparationHour:$preparationMinute"

        val recipeName = sharedPreferences.loadData("RecipeName")
        val categoryName = sharedPreferences.loadData("SelectedCategory")
        val ingredients = sharedPreferences.loadData("Ingredients")
        val ins = sharedPreferences.loadData("Instructions")
        val recipePhotoPath = sharedPreferences.loadData("RecipePhotoPath")
        val bitmapPhoto = decodeBitmapFromFile(recipePhotoPath)
        val recipeRate = sharedPreferences.loadData("recipeRate")
        val videoLink = sharedPreferences.loadData("videoLink")
        //cookingTime + preparationTime + temperature

        //data class Recipe(
        //    val id: Long,
        //    val title: String,
        //    val category_name: String,
        //    val ingredients: String,
        //    val instructions: String,
        //    val temperature: String,
        //    val image: Bitmap?,
        //    val rating: String,
        //    val videoLink: String,
        //    val cookingTime: String,
        //    val prepTime: String
        //)

        val recipeId = databaseHelper.insertRecipe(recipeName, categoryName, ingredients, ins, temperature, recipePhotoPath, recipeRate, videoLink, cookingTime, preparationTime)
        val recipe = Recipe(recipeId, recipeName, categoryName, ingredients, ins, temperature, bitmapPhoto, recipeRate, videoLink, cookingTime, preparationTime)
        recipeList.add(recipe)
    }

    // Dosyadan bitmap çözme işlevi
    private fun decodeBitmapFromFile(photoPath: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(photoPath, options)
    }

    // Sıcaklık birimini al
    private fun getTemperatureUnit(): String {
        val switchTemperatureUnit = findViewById<SwitchMaterial>(R.id.switchTemperatureUnit)
        return if (switchTemperatureUnit.isChecked) {
            "F"
        } else {
            "C"
        }
    }

    // TimePicker'dan AM/PM kısmını kaldıran işlev
    private fun removeTimePickerAMPM(timePicker: TimePicker) {
        val id = resources.getIdentifier("amPm", "id", "android")
        val amPmView = timePicker.findViewById<View>(id)
        if (amPmView != null) {
            (amPmView.parent as LinearLayout).removeView(amPmView)
        }
    }
}
