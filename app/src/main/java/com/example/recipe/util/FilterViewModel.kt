package com.example.recipe.util

import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {
    // Define your mutable state variables here
    val addedTags: MutableSet<String> = mutableSetOf()
    val removedTags: MutableSet<String> = mutableSetOf()
    val selectedChips: MutableSet<String> = mutableSetOf()
}
