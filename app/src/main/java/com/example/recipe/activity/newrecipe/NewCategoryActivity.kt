package com.example.recipe.activity.newrecipe

import DatabaseHelper
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout
import android.view.Gravity
import android.widget.FrameLayout
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.model.Category
import com.example.recipe.adapter.CategoryAdapter
import com.example.recipe.util.GridSpacingItemDecoration
import com.example.recipe.R
import com.example.recipe.activity.home.MainActivity
import com.example.recipe.util.SharedPreferencesHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NewCategoryActivity : AppCompatActivity() {

    private lateinit var dialogViewCategoryPhoto: ImageView
    private lateinit var categoryNameTextView: TextView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var btnAddCategory: Button
    private lateinit var categoryList: MutableList<Category>
    private lateinit var etCategoryDescription: TextView
    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var btnCancelRecipe: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_category)

        // Arka plan rengini ayarla ve ActionBar'ı gizle
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)

        // UI elemanlarını tanımla
        btnAddCategory = findViewById(R.id.btnAddCategory)
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryList = mutableListOf()
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        btnCancelRecipe.setOnClickListener {
            // İptal butonuna tıklanıldığında onaylama dialogunu göster
            showCancelConfirmationDialog()
        }

        // RecyclerView için ayarlamaları yap ve kategorileri yükle
        setupRecyclerView(categoryRecyclerView)
        // Yeni kategori ekle butonuna tıklanıldığında işlemleri gerçekleştir
        addCategoryClick()
    }

    // İptal işlemi onaylama dialogunu göster
    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Kullanıcı "Yes" butonuna tıkladığında yapılacak işlemler
            sharedPreferences.deleteData("videoLink")

            // Ana ekrana geri dön
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
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

    // RecyclerView için ayarlamaları yap ve kategorileri yükle
    fun setupRecyclerView(recyclerView: RecyclerView) {
        categoryAdapter = CategoryAdapter(categoryList, this)
        categoryRecyclerView.adapter = categoryAdapter
        val spanCount = 2 // Sütun sayısını istediğiniz değere göre ayarlayın

        // GridLayoutManager oluşturulması
        val layoutManager = GridLayoutManager(this, spanCount)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        // GridSpacingItemDecoration oluşturulması
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val includeEdge = true // Kenarları da dahil etmek isterseniz true, sadece iç boşluk isterseniz false olarak ayarlayabilirsiniz
        val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, includeEdge)

        // RecyclerView'a GridSpacingItemDecoration eklenmesi
        recyclerView.addItemDecoration(itemDecoration)
        loadCategories()
    }

    // Kategorileri yükle
    private fun loadCategories() {
        val databaseHelper = DatabaseHelper(this)
        // Veritabanından kategorileri al
        val categories = databaseHelper.getAllCategories()

        // Kategorileri categoryList listesine ekle
        categoryList.addAll(categories)

        // Adapter'i güncelle
        categoryAdapter.notifyDataSetChanged()
    }

    // AlertDialog.Builder ve View çiftini döndüren yardımcı bir fonksiyon
    private fun builderViewPair(): Pair<AlertDialog.Builder, View> {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_category, null)
        dialogBuilder.setView(dialogView)
        return Pair(dialogBuilder, dialogView)
    }

    // Yeni kategori ekle butonuna tıklanıldığında işlemleri gerçekleştir
    private fun addCategoryClick() {
        btnAddCategory.setOnClickListener {
            // Arka plana blur animasyonu uygula
            applyBlurAnimation()

            // AlertDialog.Builder ve View çiftini al
            val (dialogBuilder, dialogView) = builderViewPair()

            // Dialog içindeki elemanları initialize et
            initializeDialogViewElements(dialogView)

            // Kategori fotoğrafına tıklanıldığında işlemleri gerçekleştir
            dialogViewCategoryPhotoClick()

            // AlertDialog.Builder için butonları ayarla
            dialogBuilderButtonSetting(dialogBuilder)

            // Dialog oluştur ve ekrana göster
            val dialog = dialogBuilder.create()
            val window = dialog.window

            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            window?.setGravity(Gravity.CENTER)
            window?.attributes?.windowAnimations = R.style.DialogAnimation

            dialog.show()
        }
    }

    // Bitmap'i dosyaya kaydeden yardımcı fonksiyon
    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String, context: Context): File? {
        val file = File(context.filesDir, fileName)
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
    private fun dialogBuilderButtonSetting(dialogBuilder: AlertDialog.Builder) {
        dialogBuilder.setPositiveButton("Devam Et") { dialog, which ->
            val databaseHelper = DatabaseHelper(this)

            val categoryName = categoryNameTextView.text.toString()
            val categoryDescription = etCategoryDescription.text.toString()
            val selectedPhoto = dialogViewCategoryPhoto.drawable
            val bitmap: Bitmap = (selectedPhoto as BitmapDrawable).bitmap

            val file = saveBitmapToFile(bitmap, "$categoryName.png", this) // saveBitmapToFile fonksiyonunu çağır


            if (file != null) {
                val categoryId = databaseHelper.insertCategory(categoryName, categoryDescription, file.absolutePath)
                val category = Category(categoryId, categoryName, categoryDescription, bitmap)
                categoryList.add(category)
                categoryAdapter.notifyDataSetChanged()
            }

            //Adding name desc, uri to the db
            //val categoryBox = createCategoryBox(selectedPhoto, categoryName)
            // Kullanıcının girdiği değerleri kullanarak ilgili işlemleri gerçekleştirebilirsiniz
            //val categoryContainer = findViewById<LinearLayout>(R.id.categoryContainer)
            //categoryContainer.addView(categoryBox)
        }

        dialogBuilder.setNegativeButton("İptal") { dialog, which ->
            // İptal butonuna tıklanıldığında yapılacak işlemleri burada tanımlayabilirsiniz
        }
    }

    // Kategori fotoğrafına tıklanıldığında işlemleri gerçekleştir
    private fun dialogViewCategoryPhotoClick() {
        dialogViewCategoryPhoto.setOnClickListener {
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

    // Dialog içindeki elemanları initialize et
    private fun initializeDialogViewElements(dialogView: View) {
        categoryNameTextView = dialogView.findViewById<EditText>(R.id.etCategoryName)
        etCategoryDescription = dialogView.findViewById<EditText>(R.id.etCategoryDescription)
        dialogViewCategoryPhoto = dialogView.findViewById<ImageView>(R.id.dialogViewCategoryPhoto)
    }

    // Blur animasyonunu uygula
    private fun applyBlurAnimation() {
        val backgroundContainer = findViewById<FrameLayout>(R.id.backgroundContainer)
        val animator = ObjectAnimator.ofFloat(backgroundContainer, "alpha", 0f, 1f)
        animator.duration = 500 // Animasyon süresini istediğiniz gibi ayarlayabilirsiniz
        animator.start()
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
                2 -> {
                    // Galeriden seçilen fotoğrafın URI'si
                    val imageUri = data?.data
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val drawable = BitmapDrawable(resources, bitmap)
                    // Seçilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                    dialogViewCategoryPhoto.setImageDrawable(drawable)
                }
                3 -> {
                    // Kameradan çekilen fotoğrafın URI'si
                    val imageUri = data?.data
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val drawable = BitmapDrawable(resources, bitmap)
                    // Seçilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                    dialogViewCategoryPhoto.setImageDrawable(drawable)
                    // Çekilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                }
            }
        }
    }
}


