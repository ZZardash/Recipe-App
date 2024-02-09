package com.example.recipe.activity.newrecipe
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.adapter.TagsAdapter
import com.example.recipe.model.Tag
import com.example.recipe.util.SharedPreferencesHelper

class TagsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences:SharedPreferencesHelper
    private lateinit var recyclerViewDiet: RecyclerView
    private lateinit var recyclerViewCuisine: RecyclerView
    private lateinit var recyclerViewNutrition: RecyclerView
    private lateinit var recyclerViewTag: RecyclerView

    private lateinit var tagsAdapterDiet: TagsAdapter
    private lateinit var tagsAdapterCuisine: TagsAdapter
    private lateinit var tagsAdapterNutrition: TagsAdapter
    private lateinit var tagsAdapterTag: TagsAdapter

    private lateinit var newPageButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)

        // ActionBar'ı gizle ve arkaplan rengini ayarla
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        sharedPreferences = SharedPreferencesHelper(this)

        recyclerViewDiet = findViewById(R.id.recyclerViewDiet)
        recyclerViewCuisine = findViewById(R.id.recyclerViewCuisine)
        recyclerViewNutrition = findViewById(R.id.recyclerViewNutrition)
        recyclerViewTag = findViewById(R.id.recyclerViewTag)

        newPageButton = findViewById(R.id.btnSwitchActivity)
        newPageButtonClick(newPageButton)

        setupRecyclerView(recyclerViewDiet)
        setupRecyclerView(recyclerViewCuisine)
        setupRecyclerView(recyclerViewNutrition)
        setupRecyclerView(recyclerViewTag)

        // Initialize Tags Adapters with sample data
        tagsAdapterDiet = TagsAdapter(getDietSampleTags(), this)
        tagsAdapterCuisine = TagsAdapter(getCuisineSampleTags(), this)
        tagsAdapterNutrition = TagsAdapter(getNutritionSampleTags(), this)
        tagsAdapterTag = TagsAdapter(getTagSampleTags(), this)

        // Set adapters for RecyclerViews
        recyclerViewDiet.adapter = tagsAdapterDiet
        recyclerViewCuisine.adapter = tagsAdapterCuisine
        recyclerViewNutrition.adapter = tagsAdapterNutrition
        recyclerViewTag.adapter = tagsAdapterTag
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val spanCount = 1
        val layoutManager = GridLayoutManager(this, spanCount, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val horizontalSpacing = resources.getDimensionPixelSize(R.dimen.horizontal_spacing)
        val itemDecoration = HorizontalSpacingItemDecoration(horizontalSpacing)
        recyclerView.addItemDecoration(itemDecoration)
    }


    class HorizontalSpacingItemDecoration(
        private val horizontalSpacing: Int
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position != RecyclerView.NO_POSITION) {
                outRect.right = horizontalSpacing
                if (position == 0) {
                    outRect.left = horizontalSpacing // Apply additional left margin for the first item
                }
            }
        }
    }

    private fun newPageButtonClick(newPageButton: Button) {
        newPageButton.setOnClickListener {
            // Get selected tags from all RecyclerViews
            val selectedTags = mutableListOf<String>()

            selectedTags.addAll(tagsAdapterDiet.getSelectedTags())
            selectedTags.addAll(tagsAdapterCuisine.getSelectedTags())
            selectedTags.addAll(tagsAdapterNutrition.getSelectedTags())
            selectedTags.addAll(tagsAdapterTag.getSelectedTags())

            // Save all selected tags in SharedPreferences
            saveSelectedTags(selectedTags)

            // Start the new activity
            val intent = Intent(this, OvenActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun saveSelectedTags(selectedTags: List<String>) {
        // Use SharedPreferences to save all selected tags together
        val tagsString = selectedTags.joinToString(separator = ",")
        sharedPreferences.saveData("selected_tags", tagsString)
    }



    private fun getDietSampleTags(): List<Tag> {
        // Sample data for tags
        return listOf(
            Tag(1,"Gluten Free", R.drawable.gluten_free_icon),
            Tag(1,"Keto", R.drawable.keto),
            Tag(1,"Vegetarian", R.drawable.vegeterian),
            Tag(1,"Low Carb", R.drawable.lowcarb),
            Tag(1,"Paleo", R.drawable.paleo)
            // Add more tags as needed
        )
    }
    private fun getNutritionSampleTags(): List<Tag> {
        // Sample data for tags
        return listOf(
            Tag(1,"High Fiber", R.drawable.highfiber),
            Tag(1,"High Vitamin", R.drawable.highvitamin),
            Tag(1,"Low Energy", R.drawable.lowenergy),
            Tag(1,"Low Fat", R.drawable.lowfat),
            Tag(1,"Low Sodium", R.drawable.lowsodium)
            // Add more tags as needed
        )
    }
    private fun getCuisineSampleTags(): List<Tag> {
        // Sample data for tags
        return listOf(
            Tag(1,"Spanish", R.drawable.spanish),
            Tag(1,"Turkish", R.drawable.turkish),
            Tag(1,"Asian", R.drawable.asian),
            Tag(1,"American", R.drawable.american),
            Tag(1,"Mexican", R.drawable.mexican)
            // Add more tags as needed
        )
    }

    private fun getTagSampleTags(): List<Tag> {
        // Sample data for tags
        return listOf(
            Tag(1,"Homemade", R.drawable.homemade),
            Tag(1,"Soup", R.drawable.soup),
            Tag(1,"Main Course", R.drawable.maincourse),
            Tag(1,"Appetizer", R.drawable.appetizer),
            Tag(1,"Sea Food", R.drawable.seafood)
            // Add more tags as needed
        )
    }
}




