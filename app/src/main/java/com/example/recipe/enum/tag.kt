package com.example.recipe.enum


enum class Diet(override val displayName: String): BottomSheetFilterFragment.DisplayNameProvider {
    GLUTEN_FREE("Gluten Free"),
    KETO("Keto"),
    VEGETARIAN("Vegetarian"),
    LOW_CARB("Low Carb"),
    PALEO("Paleo");

    companion object {
        fun getDiets(): List<Diet> {
            return enumValues<Diet>().toList()
        }
    }
}

enum class Nutrition(override val displayName: String): BottomSheetFilterFragment.DisplayNameProvider {
    HIGH_FIBER("High Fiber"),
    HIGH_VITAMIN("High Vitamin"),
    LOW_ENERGY("Low Energy"),
    LOW_FAT("Low Fat"),
    LOW_SODIUM("Low Sodium");

    companion object {
        fun getNutritions(): List<Nutrition> {
            return enumValues<Nutrition>().toList()
        }
    }
}

enum class Cuisine(override val displayName: String):
    BottomSheetFilterFragment.DisplayNameProvider {
    SPANISH("Spanish"),
    TURKISH("Turkish"),
    ASIAN("Asian"),
    AMERICAN("American"),
    MEXICAN("Mexican");

    companion object {
        fun getCuisines(): List<Cuisine> {
            return enumValues<Cuisine>().toList()
        }
    }
}

enum class Tags(override val displayName: String): BottomSheetFilterFragment.DisplayNameProvider {
    HOMEMADE("Homemade"),
    SOUP("Soup"),
    MAIN_COURSE("Main Course"),
    APPETIZER("Appetizer"),
    SEA_FOOD("Sea Food");

    companion object {
        fun getTags(): List<Tags> {
            return enumValues<Tags>().toList()
        }
    }
}
