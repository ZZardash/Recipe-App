package com.example.recipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.model.Ingredient

class ViewIngredientAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<ViewIngredientAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTextView: TextView = itemView.findViewById(R.id.ingredientTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_ingredient_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        val formattedIngredient = "•  ${ingredient.quantity} ${ingredient.unit} of ${ingredient.name} "
        holder.textTextView.text = formattedIngredient
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}
