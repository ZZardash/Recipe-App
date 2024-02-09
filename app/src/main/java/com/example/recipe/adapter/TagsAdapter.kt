package com.example.recipe.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.model.Tag

class TagsAdapter(private val tagList: List<Tag>, private val context: Context) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define your tag item views here
        val tagName: TextView = itemView.findViewById(R.id.tagName)
        val tagCheckBox: CheckBox = itemView.findViewById(R.id.checkBoxTag)
        val btnCardView: CardView = itemView.findViewById(R.id.btnTagCardView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIconTag)
        val tagImage: ImageView = itemView.findViewById(R.id.circleImageView)

        init {
            btnCardView.setOnLongClickListener {
                // Long-click operations
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

            // Additional long-click operations here

            return true
        }

        fun bind(tag: Tag) {
            deleteIcon.visibility = View.INVISIBLE
            // Bind other tag item properties if needed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the tag item view layout (item_tag_view.xml)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        return ViewHolder(view)
    }

    private val selectedPositions = mutableSetOf<Int>()

    fun getSelectedTags(): List<String> {
        // Returns a list of selected tags' texts
        return tagList.filterIndexed { index, _ -> selectedPositions.contains(index) }
            .map { it.text }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tagList[position]
        holder.bind(tag)

        val tagId = tag.id
        val tagText = tag.text
        val tagPhoto = tag.photoResourceId

        holder.tagName.text = tagText
        holder.tagImage.setImageResource(tagPhoto)
        holder.tagCheckBox.isChecked = selectedPositions.contains(position)

        holder.btnCardView.setOnClickListener {
            // Toggle CheckBox state and update selected positions
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position)
            } else {
                selectedPositions.add(position)
            }
            notifyDataSetChanged() // Notify adapter to redraw views
        }


        holder.deleteIcon.setOnClickListener {
            // Handle delete action for tags if needed
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }
}

