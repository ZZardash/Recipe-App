package com.example.recipe.activity

import DatabaseHelper
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.recipe.R

class RecipePageActivity : AppCompatActivity() {

    private lateinit var ingredientsButton: Button
    private lateinit var preparationButton: Button
    private lateinit var contentLayout: FrameLayout
    private lateinit var recipeTitle: TextView
    private lateinit var dynamicLayout: LinearLayout
    private lateinit var ingredientsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_page)
        val databaseHelper = DatabaseHelper(this)

        databaseHelper.showTableColumns()
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initializeUI()

        val (title, image, recipeId) = getIntentData(databaseHelper)

        val ingredients = databaseHelper.getRecipeColumnData(recipeId, "ingredients")
        val ingredientsArray: Array<String> = ingredients.toString().split(",").toTypedArray()

        val instructions = databaseHelper.getRecipeColumnData(recipeId, "instructions").toString()
        println(instructions)

        recipeTitle.text = title
        recipeTitle.background = BitmapDrawable(resources, image)

        selectButton(ingredientsButton)
        showIngredients(ingredientsArray)
        ingredientsButton.setOnClickListener {
            showIngredients(ingredientsArray)
            setIngredientsLayer()
            selectButton(ingredientsButton)

        }
        preparationButton.setOnClickListener {
            showPreparation()
            showInstructions(instructions)
            selectButton(preparationButton)
        }
    }

    private fun getIntentData(databaseHelper: DatabaseHelper): Triple<String, Bitmap, Long> {
        val title = intent.getStringExtra("recipeTitle").toString()
        val image = databaseHelper.getRecipeBitmap(title)!! //Fetching the image with using getRecipeBitmap
        val defValue = 0L
        val recipeId = intent.getLongExtra("recipeId", defValue)
        return Triple(title, image, recipeId)
    }

    private fun initializeUI() {
        ingredientsButton = findViewById(R.id.ingredientsButton)
        preparationButton = findViewById(R.id.preparationButton)
        contentLayout = findViewById(R.id.contentLayout)
        recipeTitle = findViewById(R.id.recipeTitle)
        dynamicLayout = findViewById(R.id.dynamicLayout)
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
        val layoutParams = ingredientsTextView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        ingredientsTextView.layoutParams = layoutParams
    }

    //this funcitons is gonna add inside view_recipe_ingredients
    private fun showIngredients(ingredients: Array<String>) {
        // Create a new TextView for ingredients if it doesn't exist
        if (!::ingredientsTextView.isInitialized) {
            ingredientsTextView = TextView(this)
            ingredientsTextView.setTextColor(Color.WHITE)
            ingredientsTextView.setBackgroundColor(Color.TRANSPARENT)
            ingredientsTextView.setTextSize(14f)
            ingredientsTextView.setPadding(8, 25, 8, 8)
            ingredientsTextView.maxLines = 10
            ingredientsTextView.ellipsize = TextUtils.TruncateAt.END
            ingredientsTextView.movementMethod = ScrollingMovementMethod.getInstance()
            ingredientsTextView.isVerticalScrollBarEnabled = true

            // Set the custom font
            val customTypeface = ResourcesCompat.getFont(this, R.font.montserrat_bold)
            ingredientsTextView.typeface = customTypeface
        }

        // Set the ingredients array as the text content
        val ingredientsWithDots = ingredients.map { "\u2022 $it" }
        ingredientsTextView.text = ingredientsWithDots.joinToString("\n")

        // Update the contentLayout with the ingredientsTextView
        contentLayout.removeAllViews()
        contentLayout.addView(ingredientsTextView)
    }

    private fun showInstructions(instructions: String) {
        val preparationItemView = TextView(this)
        preparationItemView.setTextColor(Color.WHITE)
        preparationItemView.setBackgroundColor(Color.TRANSPARENT)
        preparationItemView.setTextSize(14f)
        preparationItemView.setPadding(8, 25, 8, 8)
        preparationItemView.maxLines = 10
        preparationItemView.ellipsize = TextUtils.TruncateAt.END
        preparationItemView.movementMethod = ScrollingMovementMethod.getInstance()
        preparationItemView.isVerticalScrollBarEnabled = true

        // Set the custom font
        val customTypeface = ResourcesCompat.getFont(this, R.font.montserrat_bold)
        preparationItemView.typeface = customTypeface

        // Set the instructions as the text content
        preparationItemView.text = instructions

        // Add the preparationItemView to the dynamicLayout
        dynamicLayout.addView(preparationItemView)
    }


    private fun showPreparation() {
        // Remove the ingredientsTextView from the contentLayout if it exists
        if (::ingredientsTextView.isInitialized) {
            contentLayout.removeView(ingredientsTextView)
        }

        // Show the preparation layout or perform any desired action for the "Preparation" button
        // Add your logic here
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}
