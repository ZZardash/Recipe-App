package com.example.recipe.activity.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import com.example.recipe.R
import com.example.recipe.activity.newrecipe.NewRecipeActivity
import com.example.recipe.activity.viewrecipe.ViewCategoriesActivity
import com.example.recipe.util.SharedPreferencesHelper

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var imgAddRecipe: ImageView
    private lateinit var imgViewRecipe: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // SharedPreferencesHelper sınıfı ile veri paylaşımı için nesne oluşturuluyor.
        sharedPreferences = SharedPreferencesHelper(this)

        // Arayüzdeki bileşenlere erişim için gerekli ImageView nesneleri tanımlanıyor.
        imgAddRecipe = findViewById(R.id.imgAddRecipe)
        imgViewRecipe = findViewById(R.id.imgViewRecipe)

        addRecipeIntent()

        // Yeni bir geri tuşu callback'i ekleniyor.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri tuşu tıklandığında herhangi bir işlem gerçekleşmeyecek.
                // Gerekirse geri tuşu işlemleri burada implemente edilebilir.
            }
        })
    }

    // Yeni bir tarif eklemek için intent oluşturan ve ilgili aktiviteye geçişi sağlayan fonksiyon.
    private fun addRecipeIntent() {
        imgAddRecipe.setOnClickListener {
            // SharedPreferences'te "videoLink" adlı veriyi sil.
            sharedPreferences.deleteData("videoLink")

            // Yeni bir tarif eklemek için NewRecipeActivity'ye geçiş yapılıyor.
            val intent = Intent(this, NewRecipeActivity::class.java)
            startActivity(intent)

            // Ekran geçiş animasyonları ayarlanıyor.
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Tarif kategorilerini görüntülemek için intent oluşturan ve ilgili aktiviteye geçişi sağlayan fonksiyon.
        imgViewRecipe.setOnClickListener {
            val intent = Intent(this, ViewCategoriesActivity::class.java)
            startActivity(intent)

            // Ekran geçiş animasyonları ayarlanıyor.
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
