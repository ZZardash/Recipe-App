//Second Page (New Recipe)
package com.example.recipe.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recipe.R
import com.example.recipe.util.SharedPreferencesHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.RatingBar
import android.widget.Toast

class NewRecipeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var addRecipePhoto: ImageView
    private lateinit var btnNewCategory: Button
    private lateinit var recipeName: EditText
    private lateinit var btnCancelRecipe: Button
    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)

        //Background = transparent, Action Bar and ActionBarTitle disabled.
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //Defining the sharedPreferences class 
        sharedPreferences = SharedPreferencesHelper(this)
        addRecipePhoto = findViewById(R.id.addRecipePhoto)
        btnNewCategory = findViewById(R.id.btnNewCategory)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        ratingBar = findViewById(R.id.ratingBar)
        btnCancelRecipe.setOnClickListener {
            showCancelConfirmationDialog()
        }

        newCategoryButtonClick(btnNewCategory)
        addRecipePhotoClick()
        setupRatingBar()
    }

    private fun setupRatingBar() {
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Save the rating to SharedPreferences
            sharedPreferences.saveData("recipeRate", rating.toString())
            println(rating)
            // You can display a Toast or perform other actions as needed
            Toast.makeText(this, "Rating: $rating", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Kullanıcı "Yes" butonuna tıkladığında yapılacak işlemler
            // Örneğin, Main Activity'e dönüş işlemi
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left,)
            finish() // Bu aktiviteyi kapat
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // Kullanıcı "No" butonuna tıkladığında yapılacak işlemler
            dialog.dismiss() // Dialogu kapat
        }
        val alertDialog = alertDialogBuilder.create()

        // Ekranın ortasına kayma animasyonu eklemek için aşağıdaki satırı kullanabilirsiniz
        alertDialog.window?.attributes?.windowAnimations = 0
        alertDialog.show()
    }

    private fun newCategoryButtonClick(btnNewCategory: Button) {
        btnNewCategory.setOnClickListener {
            //Saving recipe name to RecipeData class
            recipeName = findViewById(R.id.etRecipeName)

            //Taking Recipe name, photo and saving it to the sharedPreferences
            val enteredRecipeName = recipeName.text.toString()

            if (enteredRecipeName.isEmpty()) {
                // Show a toast message indicating that the recipe name cannot be empty
                Toast.makeText(this, "Recipe name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                val photo = addRecipePhoto.drawable
                val selectedPhoto: Bitmap = (photo as BitmapDrawable).bitmap
                val file = saveBitmapToFile(selectedPhoto, "$enteredRecipeName.png", this)

                if (file != null) {
                    sharedPreferences.saveData("RecipePhotoPath", file.absolutePath)
                }
                sharedPreferences.saveData("RecipeName", enteredRecipeName)
                //Transition
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

    private fun addRecipePhotoClick() {
        addRecipePhoto.setOnClickListener {
            // Fotoğraf seçme işlemlerini burada gerçekleştirin
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            val permissionGranted = permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
            if (permissionGranted) {
                openGalleryOrCamera()
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1)
            }
        }
    }

    private fun openGalleryOrCamera() {
        val options = arrayOf<CharSequence>("Galeri", "Kamera")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fotoğraf Seç")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 2)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 3)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == RESULT_OK) {
                when (requestCode) {
                    2 -> { // Gallery
                        val imageUri = data?.data
                        if (imageUri != null) {
                            val inputStream = contentResolver.openInputStream(imageUri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            val drawable = BitmapDrawable(resources, bitmap)
                            addRecipePhoto.setImageDrawable(drawable)
                        }
                    }

                    3 -> { // Camera
                        // Check if the data parameter is null
                        if (data != null && data.extras != null) {
                            // Extract the bitmap from the camera intent's extras
                            val bitmap = data.extras?.get("data") as Bitmap
                            val drawable = BitmapDrawable(resources, bitmap)
                            addRecipePhoto.setImageDrawable(drawable)
                        }
                    }
                }
            }
        }
    }

