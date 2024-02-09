package com.example.recipe.util


import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.example.recipe.R
import com.example.recipe.activity.newrecipe.IngredientsActivity

class CategoryItemClickListener(private val activity: Activity) : View.OnClickListener {

    override fun onClick(view: View) {


        val categoryNameTextView = view.findViewById<TextView>(R.id.categoryName)
        val categoryName = categoryNameTextView.text.toString()

        // SharedPreferences kullanarak seçilen kategori adını kaydetme
        val sharedPreferencesHelper = SharedPreferencesHelper(activity)
        val sharedPreferences = sharedPreferencesHelper.getSharedPreferences()

        // Seçilen kategori adını kaydetme
        sharedPreferencesHelper.saveData("SelectedCategory", categoryName) //(OK)

        // Veriyi kontrol etmek için geri alın
        val savedCategory = sharedPreferencesHelper.loadData("SelectedCategory")
        val savedName = sharedPreferencesHelper.loadData("RecipeName")
        println(savedCategory+savedName) // Debug (OK)

        // Transition
        val intent = Intent(activity, IngredientsActivity::class.java)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
