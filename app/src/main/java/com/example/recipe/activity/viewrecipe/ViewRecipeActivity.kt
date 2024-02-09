// ViewRecipeActivity.kt

package com.example.recipe.activity.viewrecipe
import BottomSheetFilterFragment
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.activity.home.MainActivity
import com.example.recipe.adapter.RecipeAdapter
import com.example.recipe.databinding.ActivityViewRecipesBinding
import com.example.recipe.model.Recipe
import com.example.recipe.util.BottomSheetSortFragment
import com.example.recipe.util.FilterViewModel
import com.example.recipe.util.GridSpacingItemDecoration

class ViewRecipeActivity : AppCompatActivity() {

    lateinit var recipeAdapter: RecipeAdapter
    lateinit var recipeList: MutableList<Recipe>
    lateinit var originalRecipeList: MutableList<Recipe>
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeTitle: TextView
    private lateinit var categoryName: String
    private lateinit var searchetIngredient: EditText
    private lateinit var btnHome: Button
    private lateinit var btnSort: Button
    private lateinit var btnFilter: Button
    private lateinit var binding: ActivityViewRecipesBinding
    private lateinit var bottomSheetFilterFragment: BottomSheetFilterFragment
    private lateinit var filterViewModel: ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSheetFilterFragment = BottomSheetFilterFragment()

        filterViewModel = ViewModelProvider(this).get(FilterViewModel::class.java)

        // ActionBar'ı gizle ve arkaplan rengini ayarla
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Intent'ten kategori adını al
        categoryName = intent.getStringExtra("selectedCategoryName").toString()

        // UI elemanlarını ilgili XML elemanlarıyla bağla
        recipeRecyclerView= binding.recipeRecyclerView
        recipeTitle= binding.recipeTitle
        searchetIngredient=binding.searchetIngredient
        btnHome=binding.btnHome
        btnSort=binding.btnSort
        btnFilter=binding.btnFilter

        recipeList = mutableListOf()
        originalRecipeList = mutableListOf()


        // DatabaseHelper sınıfını kullanmak için instance oluştur

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
        btnSort.setOnClickListener {
            showBottomSheetDialog()
        }
        btnFilter.setOnClickListener {
            showFilterBottomSheetDialog()
        }
    }
    fun resetRecipes() {
        // Reset recipes to the original list
        recipeList.clear()
        recipeList.addAll(originalRecipeList)
        recipeAdapter.notifyDataSetChanged()
    }
    fun setRecipes(filteredRecipes: List<Recipe>) {
        recipeList.clear()
        recipeList.addAll(filteredRecipes)
        recipeAdapter.notifyDataSetChanged()
    }

    private fun showFilterBottomSheetDialog() {
        bottomSheetFilterFragment.show(supportFragmentManager, bottomSheetFilterFragment.tag)
            // Assuming addedTags is a property in your FilterViewModel
            val addedTags = (filterViewModel as? FilterViewModel)?.addedTags
            println("Added Tags: $addedTags")
    }

    private fun showBottomSheetDialog() {
        val bottomSheetFragment = BottomSheetSortFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
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
        searchetIngredient.addTextChangedListener(createTextWatcher())
    }

    // TextWatcher dinleyicisi oluştur
    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Arama sorgusuna göre reçeteleri filtrele
                searchRecipes(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        }
    }

    // RecyclerView ve adaptörü ayarla
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recipeTitle.text = "$categoryName"
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

    // Tarifleri yükle
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
    private fun searchRecipes(query: String) {
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