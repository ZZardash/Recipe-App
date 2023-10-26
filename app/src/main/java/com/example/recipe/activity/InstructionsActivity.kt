package com.example.recipe.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe.R
import com.example.recipe.util.SharedPreferencesHelper

class InstructionsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var instructionsText:EditText
    private lateinit var btnToOven: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)
        btnToOven = findViewById(R.id.btnToOven)

        goToOvenPage()
    }

    private fun goToOvenPage(){
        btnToOven.setOnClickListener {
            instructionsText = findViewById(R.id.instructionsEditText)
            val instructionsText = instructionsText.text.toString()

            sharedPreferences.saveData("Instructions", instructionsText)

            val intent = Intent(this, OvenActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
