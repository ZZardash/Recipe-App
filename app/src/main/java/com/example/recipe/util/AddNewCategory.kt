package com.example.recipe.util

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe.R


class AddNewCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_view)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

}
