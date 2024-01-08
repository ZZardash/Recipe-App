package com.example.recipe.activity

import DatabaseHelper
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.adapter.ViewCategoryAdapter
import com.example.recipe.model.Category
import com.example.recipe.util.GridSpacingItemDecoration

class ViewCategoriesActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: ViewCategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryList: MutableList<Category>
    private lateinit var searchEditText: EditText
    private lateinit var originalCategoryList: MutableList<Category>
    private lateinit var btnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_category)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryList = mutableListOf()
        originalCategoryList = mutableListOf()
        searchEditText = findViewById(R.id.searchEditText)
        btnHome = findViewById(R.id.btnHome)
        btnHome.setOnClickListener{
            transitionToHome()
        }

        setupSearchListener()
        setupRecyclerView(categoryRecyclerView)
        loadCategories()
    }

    private fun transitionToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition( R.anim.slide_out_right, R.anim.slide_in_left,)
        finish() // Bu aktiviteyi kapat
    }

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(createTextWatcher())
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter recipes based on the search query
                filterRecipes(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        categoryAdapter = ViewCategoryAdapter(categoryList, this)
        recyclerView.adapter = categoryAdapter
        val spanCount = 2

        val layoutManager = GridLayoutManager(this, spanCount)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val includeEdge = true
        val itemDecoration = GridSpacingItemDecoration(spanCount, spacing, includeEdge)

        recyclerView.addItemDecoration(itemDecoration)
    }

    private fun loadCategories() {
        val databaseHelper = DatabaseHelper(this)
        val categories = databaseHelper.getAllCategories()

        categoryList.clear()
        originalCategoryList.clear()

        categoryList.addAll(categories)
        originalCategoryList.addAll(categories)

        categoryAdapter.notifyDataSetChanged()
    }

    private fun filterRecipes(query: String) {
        val filteredList = originalCategoryList.filter { category ->
            category.text.contains(query, ignoreCase = true)
        }

        categoryList.clear()
        categoryList.addAll(filteredList)
        categoryAdapter.notifyDataSetChanged()
    }
}
