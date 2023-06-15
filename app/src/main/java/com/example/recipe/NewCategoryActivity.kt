package com.example.recipe

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NewCategoryActivity : AppCompatActivity() {
    private lateinit var ivCategoryPhoto: ImageView
    private lateinit var categoryNameTextView: TextView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var btnAddCategory: Button
    private lateinit var categoryList: MutableList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_category)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Diğer kodlar...

        // 'btnAddCategory' butonuna tıklanıldığında çalışacak kod
        btnAddCategory = findViewById<Button>(R.id.btnAddCategory)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = GridLayoutManager(this, 3)
        categoryList = mutableListOf<Category>()
        categoryAdapter = CategoryAdapter(categoryList)
        categoryRecyclerView.adapter = categoryAdapter

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val gridSpacingItemDecoration = GridSpacingItemDecoration(3, spacing, true)
        categoryRecyclerView.addItemDecoration(gridSpacingItemDecoration)

        val spanCount = 3 // Sütun sayısı
        val spacingBetweenItems = resources.getDimensionPixelSize(R.dimen.grid_spacing) // Öğeler arası boşluk

        val layoutManager = GridLayoutManager(this, spanCount)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        categoryRecyclerView.layoutManager = layoutManager
        btnAddCategory.setOnClickListener {

            applyBlurAnimation()
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.dialog_add_category, null)
            dialogBuilder.setView(dialogView)

            // Kategori İsmi ve Açıklama için EditText alanlarına erişim sağlayabilirsiniz
            categoryNameTextView = dialogView.findViewById<EditText>(R.id.etCategoryName)
            val etCategoryDescription = dialogView.findViewById<EditText>(R.id.etCategoryDescription)
            ivCategoryPhoto = dialogView.findViewById<ImageView>(R.id.ivCategoryPhoto)

            ivCategoryPhoto.setOnClickListener {
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

            dialogBuilder.setPositiveButton("Devam Et") { dialog, which ->
                val categoryName = categoryNameTextView.text.toString()
                val selectedPhoto = ivCategoryPhoto.drawable

                val category = Category(categoryName, selectedPhoto)
                categoryList.add(category)
                categoryAdapter.notifyDataSetChanged()
            }

            dialogBuilder.setNegativeButton("İptal") { dialog, which ->
                // İptal butonuna tıklanıldığında yapılacak işlemleri burada tanımlayabilirsiniz
            }

            val dialog = dialogBuilder.create()
            val window = dialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent) // Arka planı transparan yapma
            window?.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) // Boyutları düzenleme
            window?.setGravity(Gravity.CENTER) // Ortada görüntülenmesi için
            window?.attributes?.windowAnimations = R.style.DialogAnimation // Animasyon eklemek için

            dialog.show()
        }

        // Go next page
        val btnIngredientsPage: Button = findViewById(R.id.btnIngredientsPage)
        btnIngredientsPage.setOnClickListener {
            val intent = Intent(this, IngredientsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun applyBlurAnimation() {
        val backgroundContainer = findViewById<FrameLayout>(R.id.backgroundContainer)
        val animator = ObjectAnimator.ofFloat(backgroundContainer, "alpha", 0f, 1f)
        animator.duration = 500 // Animasyon süresini istediğiniz gibi ayarlayabilirsiniz
        animator.start()
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
                2 -> {
                    // Galeriden seçilen fotoğrafın URI'si
                    val imageUri = data?.data
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val drawable = BitmapDrawable(resources, bitmap)
                    // Seçilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                    ivCategoryPhoto.setImageDrawable(drawable)
                }
                3 -> {
                    // Kameradan çekilen fotoğrafın URI'si
                    val imageUri = data?.data
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val drawable = BitmapDrawable(resources, bitmap)
                    // Seçilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                    ivCategoryPhoto.setImageDrawable(drawable)
                    // Çekilen fotoğrafı kullanarak istediğiniz işlemleri yapabilirsiniz
                }
            }
        }
    }
}
