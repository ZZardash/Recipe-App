// İkinci Sayfa (Yeni Tarif)
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

        // Arka plan = şeffaf, ActionBar ve ActionBar Başlığı devre dışı bırakıldı.
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // SharedPreferences sınıfını tanımla
        sharedPreferences = SharedPreferencesHelper(this)
        addRecipePhoto = findViewById(R.id.addRecipePhoto)
        btnNewCategory = findViewById(R.id.btnNewCategory)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        ratingBar = findViewById(R.id.ratingBar)
        btnCancelRecipe.setOnClickListener {
            showCancelConfirmationDialog()
        }

        // Yeni kategori butonuna tıklanıldığında işlemleri gerçekleştir
        newCategoryButtonClick(btnNewCategory)
        // Tarif fotoğrafına tıklanıldığında işlemleri gerçekleştir
        addRecipePhotoClick()
        // RatingBar'ı ayarla
        setupRatingBar()
    }

    // RatingBar'ı ayarla
    private fun setupRatingBar() {
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Oylamayı SharedPreferences'e kaydet
            sharedPreferences.saveData("recipeRate", rating.toString())
            // Rating bilgisini yazdır (debug amaçlı)
            println(rating)
            // İhtiyaç duyulan diğer işlemleri gerçekleştirebilirsiniz (örneğin, Toast mesajı gösterme)
            Toast.makeText(this, "Rating: $rating", Toast.LENGTH_SHORT).show()
        }
    }

    // İptal işlemi onaylama dialogunu göster
    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Kullanıcı "Yes" butonuna tıkladığında yapılacak işlemler
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

        // Ekranın ortasına kayma animasyonu eklemek için
        alertDialog.window?.attributes?.windowAnimations = 0
        alertDialog.show()
    }

    // Yeni kategori butonuna tıklanıldığında işlemleri gerçekleştir
    private fun newCategoryButtonClick(btnNewCategory: Button) {
        btnNewCategory.setOnClickListener {
            // RecipeName elemanını tanımla ve içeriğini al
            recipeName = findViewById(R.id.etRecipeName)
            val enteredRecipeName = recipeName.text.toString()

            if (enteredRecipeName.isEmpty()) {
                // Recipe name boş olamaz uyarısı göster
                Toast.makeText(this, "Recipe name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                // Recipe adını ve fotoğrafını SharedPreferences'e kaydet
                val photo = addRecipePhoto.drawable
                val selectedPhoto: Bitmap = (photo as BitmapDrawable).bitmap
                val file = saveBitmapToFile(selectedPhoto, "$enteredRecipeName.png", this)

                if (file != null) {
                    sharedPreferences.saveData("RecipePhotoPath", file.absolutePath)
                }
                sharedPreferences.saveData("RecipeName", enteredRecipeName)

                // Yeni kategori ekranına geçiş
                val intent = Intent(this, NewCategoryActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    // Bitmap'i dosyaya kaydeden yardımcı fonksiyon
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

    // Tarif fotoğrafına tıklanıldığında işlemleri gerçekleştir
    private fun addRecipePhotoClick() {
        addRecipePhoto.setOnClickListener {
            // Fotoğraf seçme işlemlerini gerçekleştir
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

    // Galeri veya Kamera açma işlemini gerçekleştir
    private fun openGalleryOrCamera() {
        val options = arrayOf<CharSequence>("Gallery", "Camera")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Photo")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
        }
        builder.show()
    }

    // Galeri açma işlemini gerçekleştir
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 2)
    }

    // Kamera açma işlemini gerçekleştir
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 3)
    }

    // onActivityResult fonksiyonu ile geri dönülen sonuçları işle
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                2 -> { // Galeri
                    val imageUri = data?.data
                    if (imageUri != null) {
                        val inputStream = contentResolver.openInputStream(imageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val drawable = BitmapDrawable(resources, bitmap)
                        addRecipePhoto.setImageDrawable(drawable)
                    }
                }

                3 -> { // Kamera
                    // Veri parametresinin null olup olmadığını kontrol et
                    if (data != null && data.extras != null) {
                        // Kameradan alınan bitmap'i çıkar
                        val bitmap = data.extras?.get("data") as Bitmap
                        val drawable = BitmapDrawable(resources, bitmap)
                        addRecipePhoto.setImageDrawable(drawable)
                    }
                }
            }
        }
    }
}
