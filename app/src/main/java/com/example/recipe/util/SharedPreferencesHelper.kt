package com.example.recipe.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)



    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

    fun saveData(titleData: String, stringData: String) {
        val editor = sharedPreferences.edit()
        editor.putString(titleData, stringData)
        editor.apply()
    }

    fun loadData(titleData: String): String {
        val stringData = sharedPreferences.getString(titleData, "") ?: ""
        return stringData
    }

    fun updateData(titleData: String, stringData: String) {
        saveData(titleData, stringData)
    }

}