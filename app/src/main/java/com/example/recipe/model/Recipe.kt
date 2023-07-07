package com.example.recipe.model

import android.graphics.Bitmap

data class Recipe(
    val id: Long,
    val title: String,
    val category_name: String,
    val ingredients: String,
    val instructions: String,
    val temperature: String,
    val time: String,
    val image: Bitmap?,
)

