package com.example.recipe

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.EditText
import android.view.View



class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //val recipeNameEditText: EditText = findViewById(R.id.edit_recipe_name)

    //val recipeName = recipeNameEditText.text.toString()
    val ingredientList = mutableListOf<String>()

    companion object {
        private const val DATABASE_NAME = "RecipesDB"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Tarifler tablosunu oluşturma
        val createRecipesTableQuery = "CREATE TABLE IF NOT EXISTS recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category TEXT)"
        db?.execSQL(createRecipesTableQuery)

        // Kategoriler tablosunu oluşturma
        val createCategoriesTableQuery = "CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)"
        db?.execSQL(createCategoriesTableQuery)
        //addRecipe(recipeName, selectedCategory, ingredientList)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Tabloları güncelleme işlemlerini burada gerçekleştirin
    }
    fun addRecipe(name: String, category: String, ingredients: List<String>) {
        val db = writableDatabase

        // Tarifleri tabloya ekleme
        val insertRecipeQuery = "INSERT INTO recipes (name, category) VALUES (?, ?)"
        val recipeValues = ContentValues()
        recipeValues.put("name", name)
        recipeValues.put("category", category)
        val recipeId = db.insert("recipes", null, recipeValues)

        // Malzemeleri tabloya ekleme
        val insertIngredientQuery = "INSERT INTO ingredients (recipe_id, ingredient) VALUES (?, ?)"
        for (ingredient in ingredients) {
            val ingredientValues = ContentValues()
            ingredientValues.put("recipe_id", recipeId)
            ingredientValues.put("ingredient", ingredient)
            db.insert("ingredients", null, ingredientValues)
        }

        db.close()
    }


    // İçerik satırlarını ingredientList'e ekleme işlemleri

    // Tarifi veritabanına kaydetme

}
