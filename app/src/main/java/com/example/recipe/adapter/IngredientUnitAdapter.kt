package com.example.recipe.adapter

// IngredientUnitAdapter.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.recipe.enum.IngredientQuantityUnit

class IngredientUnitAdapter(
    context: Context,
    resource: Int,
    private val units: Array<IngredientQuantityUnit>
) : ArrayAdapter<IngredientQuantityUnit>(context, resource, units) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_spinner_item, parent, false)

        val textView: TextView = view.findViewById(android.R.id.text1)
        textView.text = context.getString(units[position].resourceId)

        return view
    }
}
