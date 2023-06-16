package com.example.recipe


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText

class IngredientsActivity: AppCompatActivity() {


    private lateinit var ingredientContainer: LinearLayout
    private lateinit var addIngredientButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        ingredientContainer = findViewById(R.id.ingredientContainer)
        addIngredientButton = findViewById(R.id.addIngredientButton)

        addIngredientButton.setOnClickListener {
            addIngredientRow()
        }

        //acitivy_ingredients da ingredients kısmı genişliği
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val desiredWidth = (screenWidth * 0.8).toInt()
        linearLayout.layoutParams.width = desiredWidth
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

}
