package com.example.recipe


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.widget.Button

class NewRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val btnNewCategory: Button = findViewById(R.id.btnNewCategory)

        newCategoryButtonClick(btnNewCategory)
    }

    private fun newCategoryButtonClick(btnNewCategory: Button) {
        btnNewCategory.setOnClickListener {
            //Saving recipe name to db
            val intent = Intent(this, NewCategoryActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }
    }
}

