// Tarif Sayfası (RecipePage) Aktivitesi
package com.example.recipe.activity.viewrecipe

import DatabaseHelper
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.activity.home.MainActivity
import com.example.recipe.adapter.ViewIngredientAdapter

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

        // UI bileşenlerini başlat
        initializeUI()

        // Intent'ten verileri al ve görüntüle
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
        val time = databaseHelper.getRecipeColumnData(recipeId, "cookingTime").toString()
        val recipeRate = databaseHelper.getRecipeColumnData(recipeId, "recipeRate").toString().toFloatOrNull()
        val videoLink = databaseHelper.getRecipeColumnData(recipeId, "videoLink").toString()


        //RateBar'ı recipeRate'a ayarla
        recipeRate?.let {
            ratingBar.rating = it
        }

        // Başlık ve arkaplan resmi ayarla
        recipeTitle.text = title
        recipeTitle.background = BitmapDrawable(resources, image)

        // "İçindekiler" ve "Hazırlık" düğmeleri için işlevsellik kur
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

        // dynamicLayout için OnTouchListener ayarla
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

    private fun selectButton(button: Button, isSelected: Boolean) {
        // Başlangıç renk değerlerini ayarla
        val startColor = if (isSelected) Color.parseColor("#47A187") else ContextCompat.getColor(this, androidx.appcompat.R.color.ripple_material_dark)
        val endColor = if (isSelected) ContextCompat.getColor(this, androidx.appcompat.R.color.ripple_material_dark) else Color.parseColor("#47A187")

        // Renk değişimi için ObjectAnimator oluştur
        val colorAnimator = ObjectAnimator.ofArgb(button, "textColor", startColor, endColor)
        colorAnimator.duration = 300 // Animasyon süresi milisaniye cinsinden

        // Ölçeklendirme için ObjectAnimator oluştur
        val scaleValue = if (isSelected) 1.05f else 1.0f
        val scaleX = ObjectAnimator.ofFloat(button, View.SCALE_X, scaleValue)
        val scaleY = ObjectAnimator.ofFloat(button, View.SCALE_Y, scaleValue)

        // Animasyonları birlikte oynatmak için AnimatorSet oluştur
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
        // Eğer varsa ingredientsTextView'ı contentLayout'tan kaldır
        if (::ingredientsTextView.isInitialized) {
            contentLayout.removeView(ingredientsTextView)
        }

        // Ana layout
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.gravity = Gravity.CENTER

        // ScrollView ekleyin
        val scrollView = ScrollView(this)

        // Talimatları içeren layout
        val instructionsLayout = LinearLayout(this)
        instructionsLayout.orientation = LinearLayout.HORIZONTAL
        instructionsLayout.gravity = Gravity.CENTER

        val preparationTextView = TextView(this)
        preparationTextView.text = instructions
        preparationTextView.setTextColor(Color.WHITE)
        preparationTextView.setBackgroundColor(Color.TRANSPARENT)
        preparationTextView.setTextSize(14f)
        preparationTextView.setPadding(8, 25, 8, 8)
        preparationTextView.typeface = ResourcesCompat.getFont(this, R.font.montserrat_bold)
        preparationTextView.movementMethod = ScrollingMovementMethod.getInstance()
        preparationTextView.isVerticalScrollBarEnabled = true

        instructionsLayout.addView(preparationTextView)

        // Butonları içeren layout
        val buttonsLayout = LinearLayout(this)
        buttonsLayout.orientation = LinearLayout.HORIZONTAL
        buttonsLayout.gravity = Gravity.CENTER

        val videoLinksArray = videoLinks.split("\n")
        if (videoLinksArray.isNotEmpty()) {
            for (link in videoLinksArray) {
                // Bağlantı "youtube", "instagram" veya "tiktok" içeriyorsa kontrol et
                when {
                    link.contains("youtube", true) || link.contains("youtu.be", true) -> {
                        // YouTube düğmesi oluştur
                        addButtonWithDrawable(link, R.drawable.youtube, buttonsLayout)
                    }

                    link.contains("instagram", true) -> {
                        // Instagram düğmesi oluştur
                        addButtonWithDrawable(link, R.drawable.instagram, buttonsLayout)
                    }

                    link.contains("tiktok", true) -> {
                        // TikTok düğmesi oluştur
                        addButtonWithDrawable(link, R.drawable.tiktok, buttonsLayout)
                    }
                    else -> {
                        // Gerekirse diğer platformları ele al
                        addButtonWithDrawable(link, R.drawable.default_video, buttonsLayout)
                    }
                }
            }
        } else {
            // Video link array boşsa burada bir işlem yapabilirsiniz.
            // Örneğin, bir mesaj gösterebilir veya başka bir varsayılan işlemi gerçekleştirebilirsiniz.
            // Şu anlık bir mesaj gösterelim:
            Log.d("showPreparation", "Video link array boş.")
        }


        // Ana layout'a instructionsLayout ve buttonsLayout'u ekleyin
        mainLayout.addView(instructionsLayout)
        mainLayout.addView(buttonsLayout)

        // ScrollView içine mainLayout'u ekleyin
        scrollView.addView(mainLayout)

        // İçerik düzenini güncelle
        contentLayout.removeAllViews()
        contentLayout.addView(scrollView)
    }

    private fun deleteVideoLinkFromDatabase(link: String) {
        val databaseHelper = DatabaseHelper(this)
        val recipeId = intent.getLongExtra("recipeId", 0)

        // Retrieve the current videoLink value from the database
        val currentVideoLinks = databaseHelper.getRecipeColumnData(recipeId, "videoLink").toString()

        // Remove the specified link from the current value
        val updatedVideoLinks = currentVideoLinks.replace(link, "").trim()

        // Update the database with the modified videoLink value
        databaseHelper.updateRecipeColumnData(recipeId, "videoLink", updatedVideoLinks)
    }


    private fun showDeleteConfirmationDialog(link: String, button: Button, parentLayout: LinearLayout) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Delete Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to delete this video link?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Delete the video link from the database
            deleteVideoLinkFromDatabase(link)

            // Remove the button from the parent layout
            parentLayout.removeView(button)
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun addButtonWithDrawable(link: String, drawableResId: Int, parentLayout: LinearLayout) {

        val button = Button(this)
        button.setBackgroundResource(drawableResId)

        button.setOnClickListener {
            // Karşılık gelen bağlantıyı açmak için mantık uygula
            openVideoLink(link)
        }

        button.setOnLongClickListener {
            showDeleteConfirmationDialog(link, button, parentLayout)
            true
        }

        val params = LinearLayout.LayoutParams(
            250,
            250
        )
        params.setMargins(16, 0, 16, 0)
        button.layoutParams = params

        if (link.isNotEmpty()){
            parentLayout.addView(button)
        }
    }

    private fun openVideoLink(videoLinks: String) {
        val linksArray = videoLinks.split("\n")

        for (link in linksArray) {
            val trimmedVideoLink = link.trim()
            Log.d("VideoLink", "Video Link: $trimmedVideoLink")

            if (isValidLink(trimmedVideoLink)) {
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
                    else -> {
                        // If the link doesn't contain any of the specified platforms, consider it invalid
                        Toast.makeText(this, "This is not a valid link", Toast.LENGTH_SHORT).show()
                        return  // Return here to prevent starting the activity for an invalid link
                    }
                }

                startActivity(videoIntent)
            } else {
                // Link boşsa uyarı ver
                Toast.makeText(this, "This is not a valid link", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isValidLink(link: String): Boolean {
        // Implement your validation logic here
        // For example, you can check if the link starts with "http" or "https" to consider it a valid link
        return link.startsWith("http") || link.startsWith("https")
    }



    private fun showCombinedDetailsIngredients(ingredients: Array<String>, temperature: String, selectedTime: String) {
        // Eğer varsa mevcut çocuk görünümünü contentLayout'tan kaldır
        contentLayout.removeAllViews()

        // Birleşik detaylar ve içerik için ana LinearLayout'i oluştur
        val combinedLayout = LinearLayout(this)
        combinedLayout.orientation = LinearLayout.VERTICAL
        combinedLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Zaman ve sıcaklık için yatay LinearLayout'i oluştur
        val timeTempLayout = LinearLayout(this)
        timeTempLayout.orientation = LinearLayout.HORIZONTAL
        timeTempLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        timeTempLayout.gravity = Gravity.CENTER_VERTICAL // Dikey olarak ortala

        // Saat resmini timeTempLayout'a ekle
        val clockImageView = ImageView(this)
        clockImageView.setImageResource(R.drawable.clock)
        clockImageView.setColorFilter(Color.WHITE) // Saat resminin rengini beyaz yap
        clockImageView.setPadding(20, 0, 0, 0)
        val imageSize = resources.getDimensionPixelSize(R.dimen.clock_image_size)
        val imageMarginEnd = resources.getDimensionPixelSize(R.dimen.image_margin_end)
        clockImageView.layoutParams = LinearLayout.LayoutParams(imageSize, imageSize).apply {
            marginEnd = imageMarginEnd
        }
        timeTempLayout.addView(clockImageView)

        // Saat TextView'ini timeTempLayout'a ekle
        val timeTextView = TextView(this)
        timeTextView.text = selectedTime
        timeTextView.setTextColor(Color.WHITE)
        timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f) // Yazı tipi boyutunu 32sp olarak ayarla
        val timeTempMargin = resources.getDimensionPixelSize(R.dimen.time_temp_margin)
        timeTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginEnd = timeTempMargin // Sağa margin ekle
        }
        timeTempLayout.addView(timeTextView)

        // Sıcaklık resmini timeTempLayout'a ekle
        val temperatureImageView = ImageView(this)
        temperatureImageView.setImageResource(R.drawable.temperature)
        temperatureImageView.setColorFilter(Color.WHITE) // Sıcaklık resminin rengini beyaz yap
        val temperatureIconSize = resources.getDimensionPixelSize(R.dimen.clock_image_size)
        temperatureImageView.layoutParams = LinearLayout.LayoutParams(temperatureIconSize, temperatureIconSize)
        timeTempLayout.addView(temperatureImageView)

        // Sıcaklık TextView'ini timeTempLayout'a ekle
        val temperatureTextView = TextView(this)
        temperatureTextView.text = temperature
        temperatureTextView.setTextColor(Color.WHITE)
        temperatureTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f) // Yazı tipi boyutunu 32sp olarak ayarla
        timeTempLayout.addView(temperatureTextView)

        // timeTempLayout'ı combinedLayout'a ekle
        combinedLayout.addView(timeTempLayout)

        // Beyaz çizgi ayıran bir görünüm oluştur
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
        val adapter = ViewIngredientAdapter(ingredients)
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