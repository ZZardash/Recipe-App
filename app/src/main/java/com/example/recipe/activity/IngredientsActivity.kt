package com.example.recipe.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
import com.example.recipe.R
import com.example.recipe.util.SharedPreferencesHelper

class IngredientsActivity: AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var ingredientContainer: LinearLayout
    private lateinit var addIngredientButton: Button
    private lateinit var btnToInstructions: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)

        ingredientContainer = findViewById(R.id.ingredientContainer)
        addIngredientButton = findViewById(R.id.addIngredientButton)
        btnToInstructions = findViewById(R.id.btnToInstructions)


        addIngredientButton.setOnClickListener {
            addIngredientRow()
        }

        //acitivy_ingredients da ingredients kısmı genişliği
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val desiredWidth = (screenWidth * 0.8).toInt()
        linearLayout.layoutParams.width = desiredWidth

        slideToInstructionsPage()
    }

    private fun slideToInstructionsPage() {
        btnToInstructions.setOnClickListener {
            val ingredientList = collectEditTextValues(ingredientContainer)
            val ingredientText = ingredientList.joinToString(", ")

            sharedPreferences.saveData("Ingredients", ingredientText)

            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun addIngredientRow() {
        val ingredientRow = layoutInflater.inflate(R.layout.ingredient_row, null)
        val ingredientEditText = ingredientRow.findViewById<EditText>(R.id.ingredientEditText)
        val removeIngredientButton = ingredientRow.findViewById<Button>(R.id.removeIngredientButton)

        removeIngredientButton.setOnClickListener {
            ingredientContainer.removeView(ingredientRow)
        }

        ingredientContainer.addView(ingredientRow)
    }
    private fun collectEditTextValues(ingredientContainer: LinearLayout): List<String> {
        val ingredientList = mutableListOf<String>()
        for (i in 0 until ingredientContainer.childCount) {
            val ingredientRow = ingredientContainer.getChildAt(i)
            val ingredientEditText = ingredientRow.findViewById<EditText>(R.id.ingredientEditText)
            val ingredientText = ingredientEditText.text.toString().trim()
            if (ingredientText.isNotEmpty()) {
                ingredientList.add(ingredientText)
            }
        }
        return ingredientList
    }
}
