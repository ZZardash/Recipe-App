package com.example.recipe.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import com.example.recipe.R
import com.example.recipe.ui.theme.RecipeTheme
import com.example.recipe.util.SharedPreferencesHelper

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var imgAddRecipe: ImageView
    private lateinit var imgViewRecipe: ImageView
    private lateinit var imgSettingsRecipe: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = SharedPreferencesHelper(this)

        imgAddRecipe = findViewById(R.id.imgAddRecipe)
        imgViewRecipe = findViewById(R.id.imgViewRecipe)

        addRecipeIntent()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back press if needed
            }
        })
    }

    private fun addRecipeIntent() {
        imgAddRecipe.setOnClickListener {
            sharedPreferences.deleteData("videoLink")
            val intent = Intent(this, NewRecipeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        imgViewRecipe.setOnClickListener {
            val intent = Intent(this, ViewCategoriesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
