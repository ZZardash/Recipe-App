package com.example.recipe.activity

import DatabaseHelper
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.adapter.IngredientAdapter

class RecipePageActivity : AppCompatActivity() {

    private lateinit var ingredientsButton: Button
    private lateinit var preparationButton: Button
    private lateinit var contentLayout: FrameLayout
    private lateinit var recipeTitle: TextView
    private lateinit var dynamicLayout: LinearLayout
    private lateinit var ingredientsTextView: TextView
    private lateinit var clockImageView: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnHome: Button

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
        ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        btnHome = findViewById(R.id.btnHome)
        btnHome.setOnClickListener{
            transitionToHome()
        }

        val instructions = databaseHelper.getRecipeColumnData(recipeId, "instructions").toString()
        val temperature = databaseHelper.getRecipeColumnData(recipeId, "temperature").toString()
        val time = databaseHelper.getRecipeColumnData(recipeId, "selectedTime").toString()
        val recipeRate = databaseHelper.getRecipeColumnData(recipeId, "recipeRate").toString().toFloatOrNull()
        val videoLink = databaseHelper.getRecipeColumnData(recipeId, "videoLink").toString()


        //Set the rateBar to recipeRate
        recipeRate?.let {
            ratingBar.rating = it
        }


        // Set the title and background image
        recipeTitle.text = title
        recipeTitle.background = BitmapDrawable(resources, image)

        // Set up functionality for "Ingredients" and "Preparation" buttons
        selectButton(ingredientsButton, true)
        showCombinedDetailsIngredients(ingredientsArray, temperature, time)
        ingredientsButton.setOnClickListener {
            showCombinedDetailsIngredients(ingredientsArray, temperature, time)
            setIngredientsLayer()
            selectButton(ingredientsButton, false)
            selectButton(preparationButton, true)
        }
        preparationButton.setOnClickListener {
            showPreparation(instructions, videoLink)
            selectButton(preparationButton, false)
            selectButton(ingredientsButton, true)
        }

        // Set OnTouchListener to dynamicLayout

    }


    private fun transitionToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition( R.anim.slide_out_right, R.anim.slide_in_left,)
        finish() // Bu aktiviteyi kapat
    }
    private fun unselectButton(button: Button) {
        // İlgili butonu seçilmedi olarak işaretlemek ve rengini değiştirmek için kullanılır
        button.setBackgroundColor(Color.parseColor("#000000"))
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
        clockImageView = ImageView(this)
    }


    private fun selectButton(button: Button, isSelected:Boolean) {
        // Set initial color values
        val startColor = if (isSelected) Color.parseColor("#47A187") else ContextCompat.getColor(this, androidx.appcompat.R.color.ripple_material_dark)
        val endColor = if (isSelected) ContextCompat.getColor(this, androidx.appcompat.R.color.ripple_material_dark) else Color.parseColor("#47A187")

        // Create ObjectAnimator for color change
        val colorAnimator = ObjectAnimator.ofArgb(button, "textColor", startColor, endColor)
        colorAnimator.duration = 300 // Animation duration in milliseconds

        // Create ObjectAnimator for scaling
        val scaleValue = if (isSelected) 1.05f else 1.0f
        val scaleX = ObjectAnimator.ofFloat(button, View.SCALE_X, scaleValue)
        val scaleY = ObjectAnimator.ofFloat(button, View.SCALE_Y, scaleValue)

        // Create AnimatorSet to play animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(colorAnimator, scaleX, scaleY)
        animatorSet.start()
    }

    private fun setIngredientsLayer() {
        if (::ingredientsTextView.isInitialized) {
            val layoutParams = ingredientsTextView.layoutParams as FrameLayout.LayoutParams
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            ingredientsTextView.layoutParams = layoutParams
        }
    }

    private fun showPreparation(instructions: String, videoLinks: String) {
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

        // Create a LinearLayout to hold the buttons
        val buttonsLayout = LinearLayout(this)
        buttonsLayout.orientation = LinearLayout.HORIZONTAL
        buttonsLayout.gravity = Gravity.CENTER

        val videoLinksArray = videoLinks.split("\n")
        println(videoLinks)
        for (link in videoLinksArray) {
            // Check if the link contains "youtube", "instagram", or "tiktok"
            when {
                link.contains("youtube", true) || link.contains("youtu.be", true) -> {
                    // Create YouTube button
                    addButtonWithDrawable(link, R.drawable.youtube, buttonsLayout)
                }

                link.contains("instagram", true) -> {
                    // Create Instagram button
                    addButtonWithDrawable(link, R.drawable.instagram, buttonsLayout)
                }

                link.contains("tiktok", true) -> {
                    // Create TikTok button
                    addButtonWithDrawable(link, R.drawable.tiktok,  buttonsLayout)
                }
                else -> {
                    // Handle other platforms if needed
                addButtonWithDrawable(link, R.drawable.default_video,  buttonsLayout)
                }
            }
        }

        // Add the buttonsLayout to contentLayout
        contentLayout.addView(buttonsLayout)
    }

    private fun addButtonWithDrawable(link: String, drawableResId: Int, parentLayout: LinearLayout) {
        val button = Button(this)
        button.setBackgroundResource(drawableResId)
        button.setOnClickListener {
            // Implement logic to open the corresponding link
            openVideoLink(link)
        }
        val params = LinearLayout.LayoutParams(
            250,
            250
        )
        params.setMargins(16, 0, 16, 0)
        button.layoutParams = params

        parentLayout.addView(button)
    }

    private fun openVideoLink(videoLinks: String) {
        val linksArray = videoLinks.split("\n")

        for (link in linksArray) {
            val trimmedVideoLink = link.trim()
            Log.d("VideoLink", "Video Link: $trimmedVideoLink")

            if (trimmedVideoLink.isNotEmpty()) {
                val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmedVideoLink))

                when {
                    trimmedVideoLink.contains("youtube", true) || trimmedVideoLink.contains("youtu.be", true) -> {
                        videoIntent.setPackage("com.google.android.youtube")
                    }
                    trimmedVideoLink.contains("instagram", true) -> {
                        videoIntent.setPackage("com.instagram.android")

                    }
                    trimmedVideoLink.contains("tiktok", true) -> {
                        videoIntent.setPackage("com.zhiliaoapp.musically")
                    }
                    trimmedVideoLink.contains("tiktok", true) -> {
                        videoIntent.setPackage("com.zhiliaoapp.musically")
                    }
                    else -> {
                        // Handle other platforms if needed
                    }
                }

                if (videoIntent.resolveActivity(packageManager) != null) {
                    startActivity(videoIntent)
                } else {
                    videoIntent.setPackage(null)
                    startActivity(videoIntent)
                }
            } else {
                Toast.makeText(this, "Please enter a valid video link", Toast.LENGTH_SHORT).show()
            }
        }
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
        clockImageView.setPadding(20, 0, 0, 0)
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
        val ingredientsTitleTextView = TextView(this)
        ingredientsTitleTextView.text = "INGREDIENTS"
        ingredientsTitleTextView.setTextSize(20f)
        ingredientsTitleTextView.typeface = ResourcesCompat.getFont(this, R.font.montserrat_bold)
        ingredientsTitleTextView.setTextColor(Color.WHITE)
        val titleLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        titleLayoutParams.setMargins(20, 30, 0, 0)
        ingredientsTitleTextView.layoutParams = titleLayoutParams
        combinedLayout.addView(ingredientsTitleTextView)

// Create a RecyclerView for ingredients
        val recyclerView = RecyclerView(this)
        recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        recyclerView.setPadding(20, 0, 0, 0)
        combinedLayout.addView(recyclerView)

// Create and set the adapter for the RecyclerView
        val adapter = IngredientAdapter(ingredients)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Add the combinedLayout to your contentLayout or any other appropriate layout
        contentLayout.addView(combinedLayout)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}
