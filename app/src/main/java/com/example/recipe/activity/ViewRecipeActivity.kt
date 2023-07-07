package com.example.recipe.activity

import DatabaseHelper
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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
    private lateinit var btnAddRecipe: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipes)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        categoryName = intent.getStringExtra("selectedCategoryName").toString()
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        recipeList = mutableListOf()
        recipeTitle = findViewById(R.id.recipeTitle)
        btnAddRecipe = findViewById(R.id.btnAddRecipe)

        val databaseHelper = DatabaseHelper(this)




        setupRecyclerView(recipeRecyclerView)

        btnAddRecipe.setOnClickListener {
            val intent = Intent(this, NewRecipeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }

    fun setupRecyclerView(recyclerView: RecyclerView) {
        recipeTitle.text = "$categoryName recipes"
        recipeAdapter = RecipeAdapter(recipeList, this)
        recipeRecyclerView.adapter = recipeAdapter
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
        loadRecipes()
    }

    private fun loadRecipes() {
        // Veritabanından daha önceden elimizde olan selectedCategoryName e sahip recipeleri al
        val databaseHelper = DatabaseHelper(this)


        println(categoryName)
        val recipes = databaseHelper.getSpecificRecipes(categoryName)

        // Kategorileri categoryList listesine ekle
        recipeList.addAll(recipes)

        // Adapter'i güncelle
        recipeAdapter.notifyDataSetChanged()
    }
}
