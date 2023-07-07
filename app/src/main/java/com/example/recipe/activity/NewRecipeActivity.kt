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
import android.graphics.drawable.Drawable
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

class NewRecipeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var addRecipePhoto: ImageView
    private lateinit var placeholderDrawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)
        addRecipePhoto = findViewById(R.id.addRecipePhoto)

        val btnNewCategory: Button = findViewById(R.id.btnNewCategory)
        newCategoryButtonClick(btnNewCategory)
        addRecipePhotoClick()
    }

    private fun newCategoryButtonClick(btnNewCategory: Button) {
        btnNewCategory.setOnClickListener {
            //Saving recipe name to RecipeData class
            val recipeName: EditText = findViewById(R.id.etRecipeName)
            val enteredRecipeName = recipeName.text.toString()
            val photo = addRecipePhoto.drawable
            val selectedPhoto: Bitmap = (photo as BitmapDrawable).bitmap
            val file = saveBitmapToFile(selectedPhoto, "$enteredRecipeName.png", this)
            println(file)


            if (file != null){
                sharedPreferences.saveData("RecipePhotoPath", file.absolutePath)
            }
            sharedPreferences.saveData("RecipeName", enteredRecipeName)
            //Transition
            val intent = Intent(this, NewCategoryActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

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
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                2 -> {
                    // Galeriden seçilen fotoğrafın URI'si
                    val imageUri = data.data
                    if (imageUri != null) {
                        val inputStream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val drawable = BitmapDrawable(resources, bitmap)
                        // Seçilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                        addRecipePhoto.setImageDrawable(drawable)
                    }
                }

                3 -> {
                    // Kameradan çekilen fotoğrafın URI'si
                    val imageUri = data.data
                    if (imageUri != null) {
                        val inputStream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val drawable = BitmapDrawable(resources, bitmap)
                        // Seçilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                        addRecipePhoto.setImageDrawable(drawable)
                        // Çekilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                    }
                }
            }
        }
    }
}
