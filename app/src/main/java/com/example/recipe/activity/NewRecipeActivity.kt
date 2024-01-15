// İkinci Sayfa (Yeni Tarif)
package com.example.recipe.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.recipe.R
import com.example.recipe.util.SharedPreferencesHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts


class NewRecipeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var addRecipePhoto: ImageView
    private lateinit var btnNewCategory: Button
    private lateinit var recipeName: EditText
    private lateinit var btnCancelRecipe: Button
    private lateinit var ratingBar: RatingBar

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val drawable = BitmapDrawable(resources, bitmap)
                    addRecipePhoto.setImageDrawable(drawable)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)

        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)
        addRecipePhoto = findViewById(R.id.addRecipePhoto)
        btnNewCategory = findViewById(R.id.btnNewCategory)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        ratingBar = findViewById(R.id.ratingBar)
        btnCancelRecipe.setOnClickListener {
            showCancelConfirmationDialog()
        }

        newCategoryButtonClick(btnNewCategory)
        setupRatingBar()

        addRecipePhoto.setOnClickListener {
            launchPhotoPicker()
        }
    }

    private fun setupRatingBar() {
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            sharedPreferences.saveData("recipeRate", rating.toString())
            println(rating)
            Toast.makeText(this, "Rating: $rating", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
            finish()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = 0
        alertDialog.show()
    }

    private fun newCategoryButtonClick(btnNewCategory: Button) {
        btnNewCategory.setOnClickListener {
            recipeName = findViewById(R.id.etRecipeName)
            val enteredRecipeName = recipeName.text.toString()

            if (enteredRecipeName.isEmpty()) {
                Toast.makeText(this, "Recipe name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                val photo = addRecipePhoto.drawable
                val selectedPhoto: Bitmap = (photo as BitmapDrawable).bitmap
                val file = saveBitmapToFile(selectedPhoto, "$enteredRecipeName.png", this)

                if (file != null) {
                    sharedPreferences.saveData("RecipePhotoPath", file.absolutePath)
                }
                sharedPreferences.saveData("RecipeName", enteredRecipeName)

                val intent = Intent(this, NewCategoryActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String, context: Context): File? {
        val directory = File(context.filesDir, "category_images")
        if (!directory.exists()) {
            directory.mkdir()
        }
        val file = File(directory, fileName)
        return try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun launchPhotoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

