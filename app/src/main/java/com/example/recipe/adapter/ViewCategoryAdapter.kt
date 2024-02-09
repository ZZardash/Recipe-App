package com.example.recipe.adapter

import DatabaseHelper
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.activity.viewrecipe.ViewRecipeActivity
import com.example.recipe.model.Category

class ViewCategoryAdapter(private val itemList: MutableList<Category>, private val context: Context) : RecyclerView.Adapter<ViewCategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val btnCardView: CardView = itemView.findViewById(R.id.btnCardView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)

        init {
            btnCardView.setOnLongClickListener {
                // Handle long click actions here
                onLongClick(it)
            }
        }

        private fun onLongClick(view: View): Boolean {
            val cardView = view as CardView
            val deleteIcon = cardView.findViewById<ImageView>(R.id.deleteIcon)

            val isAnimationVisible = deleteIcon.tag as? Boolean ?: false
            if (isAnimationVisible) {
                // Revert the animation and hide the deleteIcon
                cardView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            deleteIcon.visibility = View.INVISIBLE
                            deleteIcon.tag = false
                        }
                    })
                    .start()
            } else {
                // Start the animation and show the deleteIcon
                deleteIcon.visibility = View.INVISIBLE
                cardView.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            deleteIcon.visibility = View.VISIBLE
                            deleteIcon.tag = true
                        }
                    })
                    .start()
            }

            // Perform additional long click operations here

            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    fun updateList(newList: List<Category>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
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

        holder.deleteIcon.setOnClickListener {
            // Implement deletion logic here, similar to CategoryAdapter
            val databaseHelper = DatabaseHelper(context)
            val categoryIdToDelete = category.id // Silinecek kategori ID'si

            val isDeleted = databaseHelper.deleteCategory(categoryIdToDelete)
            if (isDeleted) {
                itemList.remove(category) // Kategoriyi listeden de kaldır
                notifyItemRemoved(position) // RecyclerView'e kategori silindiğini bildir
                notifyItemRangeChanged(position, itemCount) // Kalan öğeleri güncelle
            } else {
                // Silme işlemi başarısız veya kategori bulunamadı
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
