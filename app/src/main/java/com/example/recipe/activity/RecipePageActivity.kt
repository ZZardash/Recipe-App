package com.example.recipe.activity

import DatabaseHelper
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import com.example.recipe.R
import kotlinx.android.synthetic.main.activity_new_category.textView
import org.w3c.dom.Text

class RecipePageActivity : AppCompatActivity() {



    private lateinit var ingredientsButton: Button
    private lateinit var preparationButton: Button
    private lateinit var contentLayout: FrameLayout
    private lateinit var recipeTitle: TextView
    private lateinit var dynamicLayout: LinearLayout

    private val ingredientsLayout: View by lazy {
        layoutInflater.inflate(R.layout.view_recipe_ingredients, contentLayout, false)

    }

    private val preparationLayout: View by lazy {
        layoutInflater.inflate(R.layout.view_recipe_preperation, contentLayout, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_page)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        ingredientsButton = findViewById(R.id.ingredientsButton)
        preparationButton = findViewById(R.id.preparationButton)
        contentLayout = findViewById(R.id.contentLayout)
        recipeTitle = findViewById(R.id.recipeTitle)
        dynamicLayout = findViewById(R.id.dynamicLayout)

        val databaseHelper = DatabaseHelper(this)
        val title = intent.getStringExtra("recipeTitle").toString()
        val image = databaseHelper.getRecipeBitmap(title)!! //Fetching the image with using getRecipeBitmap


        val defValue:Long = 0L
        val recipeId = intent.getLongExtra("recipeId", defValue)
        val ingredients = databaseHelper.getRecipeColumnData(recipeId, "COLUMN_INGREDIENTS")
        println(recipeId)
        println(ingredients)


        recipeTitle.text = title

        selectButton(ingredientsButton)
        ingredientsButton.setOnClickListener {
            showIngredients()
            setIngredientsLayer()
            selectButton(ingredientsButton)
        }

        preparationButton.setOnClickListener {
            showPreparation()
            selectButton(preparationButton)

        }
    }

    private fun selectButton(button: Button) {
        if (button == ingredientsButton) {
            // Selecting "Ingredients" button
            ingredientsButton.setTextColor(Color.parseColor("#47A187"))
            preparationButton.setTextColor(Color.parseColor("#FFFFFF"))
        } else if (button == preparationButton) {
            // Selecting "Preparation" button
            ingredientsButton.setTextColor(Color.parseColor("#FFFFFF"))
            preparationButton.setTextColor(Color.parseColor("#47A187"))
        }
    }

    private fun setIngredientsLayer() {
        val linearLayout = ingredientsLayout.findViewById<LinearLayout>(R.id.linearLayout)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val desiredWidth = (screenWidth * 0.6).toInt()
        linearLayout.layoutParams.width = desiredWidth
    }

    private fun showIngredients() {
        // İçerik düğmesini seçili hale getir
        ingredientsButton.isSelected = true
        preparationButton.isSelected = false

        // İçeriği değiştir
        contentLayout.removeAllViews()
        contentLayout.addView(ingredientsLayout)
    }

    private fun showPreparation() {
        // İçerik düğmesini seçili hale getir
        ingredientsButton.isSelected = false
        preparationButton.isSelected = true

        // İçeriği değiştir
        contentLayout.removeAllViews()
        contentLayout.addView(preparationLayout)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

}