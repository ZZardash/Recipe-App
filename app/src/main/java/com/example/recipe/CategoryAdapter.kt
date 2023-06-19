package com.example.recipe

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val itemList: List<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.categoryImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val btnCardView: CardView = itemView.findViewById(R.id.btnCardView)

        init {
            btnCardView.setOnClickListener {
                //Saving selected category to DB
                val context = itemView.context as? Activity
                val intent = Intent(itemView.context, IngredientsActivity::class.java)
                context?.startActivity(intent)
                context?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                // Geçiş animasyonları eklemek için overridePendingTransition kullanabilirsiniz
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.imageView.setImageDrawable(item.photo)
        holder.categoryName.text = item.text
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
