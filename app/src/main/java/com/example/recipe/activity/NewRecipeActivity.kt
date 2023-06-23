package com.example.recipe.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.widget.Button
import android.widget.EditText
import com.example.recipe.R
import com.example.recipe.util.SharedPreferencesHelper

class NewRecipeActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)

        val btnNewCategory: Button = findViewById(R.id.btnNewCategory)
        newCategoryButtonClick(btnNewCategory)
    }
    private fun newCategoryButtonClick(btnNewCategory: Button) {
        btnNewCategory.setOnClickListener {
            //Saving recipe name to RecipeData class
            val recipeName: EditText = findViewById(R.id.etRecipeName)
            val enteredRecipeName = recipeName.text.toString()
            sharedPreferences.saveData("RecipeName", enteredRecipeName)

            //Transition
            val intent = Intent(this, NewCategoryActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }
    }
}

