//First Page (Main Menu)
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
    // lateinit => will be initialize after
    private lateinit var btnAddRecipe: Button
    private lateinit var btnViewRecipe: Button

    // savedInstanceState:Bundle? => used for recreate activity or restore the state
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting the content to activity layout
        setContentView(R.layout.activity_main)

        //Finding the spesific elemnt by its id
        btnAddRecipe = findViewById(R.id.btnAddRecipe)
        btnViewRecipe = findViewById(R.id.btnViewRecipe)

        addRecipeIntent()
    }


    private fun addRecipeIntent(){
        btnAddRecipe.setOnClickListener {
            // Setting the intent that goes to another activity
            val intent = Intent(this, NewRecipeActivity::class.java)
            startActivity(intent)
            // Animation transition
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btnViewRecipe.setOnClickListener {
            val intent = Intent(this, ViewCategoriesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }

}


