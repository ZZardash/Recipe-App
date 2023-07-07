package com.example.recipe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.recipe.model.Category
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.activity.ViewRecipeActivity

class ViewCategoryAdapter(private val itemList: MutableList<Category>, private val context: Context) : RecyclerView.Adapter<ViewCategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val btnCardView: CardView = itemView.findViewById(R.id.btnCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = itemList[position]

        holder.categoryName.text = category.text
        holder.categoryImage.setImageBitmap(category.photo)

        holder.btnCardView.setOnClickListener {
            val intent = Intent(context, ViewRecipeActivity::class.java)
            intent.putExtra("selectedCategoryName", category.text)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
