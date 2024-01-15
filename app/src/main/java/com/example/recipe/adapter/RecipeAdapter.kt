package com.example.recipe.adapter

import DatabaseHelper
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import com.example.recipe.model.Recipe
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.activity.RecipePageActivity
import com.example.recipe.model.Category
import com.example.recipe.util.CategoryItemClickListener


class RecipeAdapter(private val itemList: MutableList<Recipe>, private val context: Context) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define your recipe item views here
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeName: TextView = itemView.findViewById(R.id.recipeName)
        val btnCardView: CardView = itemView.findViewById(R.id.btnRecipeCardView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIconRecipe)

        init {
            btnCardView.setOnLongClickListener {

                // Uzun tıklama işlemlerini burada gerçekleştirin
                onLongClick(it)
            }
        }

        private fun onLongClick(view: View): Boolean {
            val cardView = view as CardView

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
        fun bind(category: Recipe) {
            deleteIcon.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the recipe item view layout (item_recipe_view.xml)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_view, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = itemList[position]
        holder.bind(recipe)
        val recipeId = recipe.id
        val recipeIngredient = recipe.ingredients

        holder.recipeName.text = recipe.title
        holder.recipeImage.setImageBitmap(recipe.image)

        holder.btnCardView.setOnClickListener{
            val intent = Intent(context, RecipePageActivity::class.java)
            intent.putExtra("recipeId", recipeId)
            intent.putExtra("recipeTitle", recipe.title)
            context.startActivity(intent)
        }

        holder.deleteIcon.setOnClickListener {
            val databaseHelper = DatabaseHelper(context)
            val item = itemList[position]
            val categoryIdToDelete = item.id // Silinecek kategori ID'si

            val isDeleted = databaseHelper.deleteRecipe(categoryIdToDelete)
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
