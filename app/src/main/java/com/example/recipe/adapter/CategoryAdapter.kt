package com.example.recipe.adapter

import DatabaseHelper
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.model.Category
import com.example.recipe.R
import com.example.recipe.util.CategoryItemClickListener

class CategoryAdapter(private val itemList: MutableList<Category>, private val context: Context) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.categoryImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val btnCardView: CardView = itemView.findViewById(R.id.btnCardView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)



        init {
            btnCardView.setOnLongClickListener {

                // Uzun tıklama işlemlerini burada gerçekleştirin
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

        fun bind(category: Category) {
            deleteIcon.visibility = View.INVISIBLE
            // Gerekli verileri view'lara bağlamak için kullanılan fonksiyon
            imageView.setImageBitmap(category.photo)
            categoryName.text = category.text
            btnCardView.setOnClickListener(CategoryItemClickListener(itemView.context as Activity))
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
        holder.deleteIcon.setOnClickListener {
            val databaseHelper = DatabaseHelper(context)
            val item = itemList[position]
            val categoryIdToDelete = item.id // Silinecek kategori ID'si

            val isDeleted = databaseHelper.deleteCategory(categoryIdToDelete)
            if (isDeleted) {
                itemList.remove(item) // Kategoriyi listeden de kaldır
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




