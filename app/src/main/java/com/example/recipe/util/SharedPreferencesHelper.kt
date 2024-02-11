package com.example.recipe.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")

class SharedPreferencesHelper(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }
    // Expose the SharedPreferences publicly
    inline fun <reified T> saveClassListData(key: String, dataList: List<T>) {
        val editor = sharedPreferences.edit()
        val jsonString = Gson().toJson(dataList)
        editor.putString(key, jsonString)
        editor.apply()
    }

    inline fun <reified T> loadClassListData(key: String): List<T> {
        val jsonString = sharedPreferences.getString(key, null)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, object : TypeToken<List<T>>() {}.type)
        } else {
            emptyList()
        }
    }


    fun saveStringData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun getStringData(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // Functions for Int values
    fun saveIntData(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }
    fun loadIntData(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveData(titleData: String, stringData: String) {
        val editor = sharedPreferences.edit()
        editor.putString(titleData, stringData)
        editor.apply()
    }
    fun saveStringSetData(key: String, value: MutableSet<String>) {
        val editor = sharedPreferences.edit()
        editor.putStringSet(key, value)
        editor.apply()
    }
    fun loadStringSetData(key: String): Set<String> {
        return sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
    }

    fun loadData(titleData: String): String {
        val stringData = sharedPreferences.getString(titleData, "") ?: ""
        return stringData
    }

    fun updateData(titleData: String, stringData: String) {
        saveData(titleData, stringData)
    }

    fun deleteData(titleData: String) {
        val editor = sharedPreferences.edit()

        // Retrieve the key associated with the titleData
        val keyToRemove = sharedPreferences.all.keys.find { it == titleData }

        // Remove the key-value pair if found
        keyToRemove?.let {
            editor.remove(it)
            editor.apply()
        }
    }

}