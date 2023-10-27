package com.example.recipe.activity

import DatabaseHelper
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
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
    private lateinit var clockImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_page)
        val databaseHelper = DatabaseHelper(this)

        databaseHelper.showTableColumns()
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize UI components
        initializeUI()

        // Retrieve and display data from Intent
        val (title, image, recipeId) = getIntentData(databaseHelper)

        val ingredients = databaseHelper.getRecipeColumnData(recipeId, "ingredients")
        val ingredientsArray: Array<String> = ingredients.toString().split(",").toTypedArray()

        val instructions = databaseHelper.getRecipeColumnData(recipeId, "instructions").toString()
        val temperature = databaseHelper.getRecipeColumnData(recipeId, "temperature").toString()
        val time = databaseHelper.getRecipeColumnData(recipeId, "selectedTime").toString()

        println(time)

        // Set the title and background image
        recipeTitle.text = title
        recipeTitle.background = BitmapDrawable(resources, image)

        // Set up functionality for "Ingredients" and "Preparation" buttons
        selectButton(ingredientsButton)
        showCombinedDetailsIngredients(ingredientsArray, temperature, time)
        ingredientsButton.setOnClickListener {
            showCombinedDetailsIngredients(ingredientsArray, temperature, time)
            setIngredientsLayer()
            selectButton(ingredientsButton)
        }
        preparationButton.setOnClickListener {
            showPreparation(instructions)
            selectButton(preparationButton)
        }
    }

    private fun getIntentData(databaseHelper: DatabaseHelper): Triple<String, Bitmap, Long> {
        val title = intent.getStringExtra("recipeTitle").toString()
        val image = databaseHelper.getRecipeBitmap(title)!!
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
        clockImageView = ImageView(this)
    }

    private fun selectButton(button: Button) {
        if (button == ingredientsButton) {
            // When selecting the "Ingredients" button
            ingredientsButton.setTextColor(Color.parseColor("#47A187"))
            preparationButton.setTextColor(Color.parseColor("#FFFFFF"))
        } else if (button == preparationButton) {
            // When selecting the "Preparation" button
            ingredientsButton.setTextColor(Color.parseColor("#FFFFFF"))
            preparationButton.setTextColor(Color.parseColor("#47A187"))
        }
    }

    private fun setIngredientsLayer() {
        if (::ingredientsTextView.isInitialized) {
            val layoutParams = ingredientsTextView.layoutParams as FrameLayout.LayoutParams
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            ingredientsTextView.layoutParams = layoutParams
        }
    }

    private fun showPreparation(instructions: String) {
        // Remove the ingredientsTextView from the contentLayout if it exists
        if (::ingredientsTextView.isInitialized) {
            contentLayout.removeView(ingredientsTextView)
        }

        // Show the preparation layout or perform any desired action for the "Preparation" button
        // Set the instructions as the text content
        val customTypeface = ResourcesCompat.getFont(this, R.font.montserrat_bold)

        val preparationTextView = TextView(this)
        preparationTextView.text = instructions
        preparationTextView.setTextColor(Color.WHITE)
        preparationTextView.setBackgroundColor(Color.TRANSPARENT)
        preparationTextView.setTextSize(14f)
        preparationTextView.setPadding(8, 25, 8, 8)
        preparationTextView.typeface = customTypeface
        preparationTextView.movementMethod = ScrollingMovementMethod.getInstance()
        preparationTextView.isVerticalScrollBarEnabled = true

        // Update the contentLayout with the preparationTextView
        contentLayout.removeAllViews()
        contentLayout.addView(preparationTextView)
    }

    private fun showCombinedDetailsIngredients(ingredients: Array<String>, temperature: String, selectedTime: String) {
        // Remove the existing child view from contentLayout if it exists
        contentLayout.removeAllViews()

        // Create the parent LinearLayout for the combined details and ingredients
        val combinedLayout = LinearLayout(this)
        combinedLayout.orientation = LinearLayout.VERTICAL
        combinedLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Create the horizontal LinearLayout for the time and temperature
        val timeTempLayout = LinearLayout(this)
        timeTempLayout.orientation = LinearLayout.HORIZONTAL
        timeTempLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        timeTempLayout.gravity = Gravity.CENTER_VERTICAL // Center vertically

        // Create and add the clock icon to the timeTempLayout
        val clockImageView = ImageView(this)
        clockImageView.setImageResource(R.drawable.clock)
        clockImageView.setColorFilter(Color.WHITE) // Set the clock icon's color to white
        clockImageView.setPadding(20,0,0,0)
        val imageSize = resources.getDimensionPixelSize(R.dimen.clock_image_size)
        val imageMarginEnd = resources.getDimensionPixelSize(R.dimen.image_margin_end)
        clockImageView.layoutParams = LinearLayout.LayoutParams(imageSize, imageSize).apply {
            marginEnd = imageMarginEnd
        }
        timeTempLayout.addView(clockImageView)

        // Create and add the time TextView to the timeTempLayout
        val timeTextView = TextView(this)
        timeTextView.text = selectedTime
        timeTextView.setTextColor(Color.WHITE)
        timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f) // Set font size to 32sp
        val timeTempMargin = resources.getDimensionPixelSize(R.dimen.time_temp_margin)
        timeTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginEnd = timeTempMargin // Add margin to the right
        }
        timeTempLayout.addView(timeTextView)

        // Create and add the temperature ImageView to the timeTempLayout
        val temperatureImageView = ImageView(this)
        temperatureImageView.setImageResource(R.drawable.temperature)
        temperatureImageView.setColorFilter(Color.WHITE) // Set the temperature icon's color to white
        val temperatureIconSize = resources.getDimensionPixelSize(R.dimen.clock_image_size)
        temperatureImageView.layoutParams = LinearLayout.LayoutParams(temperatureIconSize, temperatureIconSize)
        timeTempLayout.addView(temperatureImageView)

        // Create and add the temperature TextView to the timeTempLayout
        val temperatureTextView = TextView(this)
        temperatureTextView.text = temperature
        temperatureTextView.setTextColor(Color.WHITE)
        temperatureTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f) // Set font size to 32sp
        timeTempLayout.addView(temperatureTextView)

        // Add the timeTempLayout to the combinedLayout
        combinedLayout.addView(timeTempLayout)

        // Create and add a white line separator
        val separatorView = View(this)
        separatorView.setBackgroundColor(Color.WHITE)
        val separatorHeight = resources.getDimensionPixelSize(R.dimen.separator_height)
        separatorView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            separatorHeight
        )
        combinedLayout.addView(separatorView)

        // Create and add the "INGREDIENTS" title TextView
        val customFont = ResourcesCompat.getFont(this, R.font.montserrat_bold)

        val ingredientsTitleTextView = TextView(this)
        ingredientsTitleTextView.text = "INGREDIENTS"
        ingredientsTitleTextView.setTextSize(20f)
        ingredientsTitleTextView.typeface = customFont
        ingredientsTitleTextView.setTextColor(Color.WHITE)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 30,0,0)
        ingredientsTitleTextView.layoutParams = layoutParams

        combinedLayout.addView(ingredientsTitleTextView)

        // Create a single TextView for the list of ingredients
        val ingredientsTextView = TextView(this)
        ingredientsTextView.setTextColor(Color.WHITE)
        ingredientsTextView.setBackgroundColor(Color.TRANSPARENT)
        ingredientsTextView.setTextSize(14f)
        ingredientsTextView.setPadding(20, 25, 8, 8)
        ingredientsTextView.maxLines = 10
        ingredientsTextView.ellipsize = TextUtils.TruncateAt.END
        ingredientsTextView.movementMethod = ScrollingMovementMethod.getInstance()
        ingredientsTextView.isVerticalScrollBarEnabled = true

        val customTypeface = ResourcesCompat.getFont(this, R.font.montserrat_bold)
        ingredientsTextView.typeface = customTypeface

        val ingredientsWithDots = ingredients.map { "\u2022 $it" }
        ingredientsTextView.text = ingredientsWithDots.joinToString("\n")

        combinedLayout.addView(ingredientsTextView)

        // Add the combinedLayout to your contentLayout or any other appropriate layout
        contentLayout.addView(combinedLayout)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}
