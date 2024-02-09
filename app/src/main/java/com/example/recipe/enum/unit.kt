package com.example.recipe.enum

import com.example.recipe.R

enum class IngredientQuantityUnit(val resourceId: Int) {
    GRAM(R.string.ingredient_quantity_unit_gram),
    KILOGRAM(R.string.ingredient_quantity_unit_kilogram),
    LITER(R.string.ingredient_quantity_unit_liter),
    TEASPOON(R.string.ingredient_quantity_unit_teaspoon),
    TABLESPOON(R.string.ingredient_quantity_unit_tablespoon),
    CUP(R.string.ingredient_quantity_unit_cup),
    PIECE(R.string.ingredient_quantity_unit_piece),
    SLICE(R.string.ingredient_quantity_unit_slice),
    PINCH(R.string.ingredient_quantity_unit_pinch),
    MILLILITER(R.string.ingredient_quantity_unit_milliliter),
    POUND(R.string.ingredient_quantity_unit_pound),
    GALLON(R.string.ingredient_quantity_unit_gallon),
    QUART(R.string.ingredient_quantity_unit_quart),
    PINT(R.string.ingredient_quantity_unit_pint);
}
