package com.example.recipe.activity

import DatabaseHelper
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.recipe.model.Category
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.adapter.ViewCategoryAdapter
import com.example.recipe.model.Recipe
import com.example.recipe.util.GridSpacingItemDecoration

class ViewCategoriesActivity : AppCompatActivity() {



    private lateinit var categoryAdapter: ViewCategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryList: MutableList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_category)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryList = mutableListOf()

        setupRecyclerView(categoryRecyclerView)

    }

    fun setupRecyclerView(recyclerView: RecyclerView) {
        categoryAdapter = ViewCategoryAdapter(categoryList, this)
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


    private fun loadCategories() {
        val databaseHelper = DatabaseHelper(this)
        // Veritabanından kategorileri al
        val categories = databaseHelper.getAllCategories()

        // Kategorileri categoryList listesine ekle
        categoryList.addAll(categories)

        // Adapter'i güncelle
        categoryAdapter.notifyDataSetChanged()
    }

}