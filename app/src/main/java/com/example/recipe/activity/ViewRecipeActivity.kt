// ViewRecipeActivity.kt

package com.example.recipe.activity

import DatabaseHelper
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.adapter.RecipeAdapter
import com.example.recipe.model.Recipe
import com.example.recipe.util.GridSpacingItemDecoration

class ViewRecipeActivity : AppCompatActivity() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var recipeTitle: TextView
    private lateinit var categoryName: String
    private lateinit var searchEditText: EditText
    private lateinit var originalRecipeList: MutableList<Recipe>
    private lateinit var btnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipes)

        // ActionBar'ı gizle ve arkaplan rengini ayarla
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Intent'ten kategori adını al
        categoryName = intent.getStringExtra("selectedCategoryName").toString()

        // UI elemanlarını ilgili XML elemanlarıyla bağla
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        recipeList = mutableListOf()
        originalRecipeList = mutableListOf()
        recipeTitle = findViewById(R.id.recipeTitle)
        searchEditText = findViewById(R.id.searchEditText)
        btnHome = findViewById(R.id.btnHome)

        // DatabaseHelper sınıfını kullanmak için instance oluştur
        val databaseHelper = DatabaseHelper(this)

        // Arama işlevselliği için dinleyiciyi ayarla
        setupSearchListener()

        // RecyclerView ve adaptörü ayarla
        setupRecyclerView(recipeRecyclerView)

        // Reçeteleri yükle
        loadRecipes()

        // Ana ekrana geçiş için butonun dinleyicisini ayarla
        btnHome.setOnClickListener {
            transitionToHome()
        }
    }

    // Ana ekrana geçiş metodu
    private fun transitionToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
        finish() // Bu aktiviteyi kapat
    }

    // Arama işlevselliği için TextWatcher dinleyicisi ayarla
    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(createTextWatcher())
    }

    // TextWatcher dinleyicisi oluştur
    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Arama sorgusuna göre reçeteleri filtrele
                filterRecipes(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        }
    }

    // RecyclerView ve adaptörü ayarla
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recipeTitle.text = "$categoryName tarifleri"
        recipeAdapter = RecipeAdapter(recipeList, this)
        recyclerView.adapter = recipeAdapter
        val spanCount = 2

        val layoutManager = GridLayoutManager(this, spanCount)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val includeEdge = true
        val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, includeEdge)

        recyclerView.addItemDecoration(itemDecoration)
    }

    // Reçeteleri yükle
    private fun loadRecipes() {
        val databaseHelper = DatabaseHelper(this)
        val recipes = databaseHelper.getSpecificRecipes(categoryName)

        // Listeleri temizle ve reçeteleri ekle
        recipeList.clear()
        originalRecipeList.clear()

        recipeList.addAll(recipes)
        originalRecipeList.addAll(recipes)

        // Adaptörü güncelle
        recipeAdapter.notifyDataSetChanged()
    }

    // Reçeteleri filtrele
    private fun filterRecipes(query: String) {
        // Orijinal reçete listesinde sorguya uyanları filtrele
        val filteredList = originalRecipeList.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true)
        }

        // Reçete listesini güncelle ve adaptörü bilgilendir
        recipeList.clear()
        recipeList.addAll(filteredList)
        recipeAdapter.notifyDataSetChanged()
    }
}
