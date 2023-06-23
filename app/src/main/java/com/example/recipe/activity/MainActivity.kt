package com.example.recipe.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.recipe.R
import com.example.recipe.ui.theme.RecipeTheme

class MainActivity : ComponentActivity() {

    private lateinit var btnAddRecipe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Layout and other components here
                }
            }
        }

        setContentView(R.layout.activity_main)

        btnAddRecipe = findViewById(R.id.btnAddRecipe)
        addRecipeIntent()


    }


    private fun addRecipeIntent(){
        btnAddRecipe.setOnClickListener {
            val intent = Intent(this, NewRecipeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

}


