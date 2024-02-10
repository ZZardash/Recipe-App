package com.example.recipe.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

data class Category(val id: Long, val text: String,val description: String, val photo: Bitmap?){
}
