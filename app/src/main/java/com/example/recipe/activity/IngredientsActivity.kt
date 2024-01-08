package com.example.recipe.activity

import MyAdapter
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.adapter.CategoryAdapter
import com.example.recipe.models.ItemsDataClass
import com.example.recipe.util.SharedPreferencesHelper
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.*


class IngredientsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var addIngredientButton: Button
    private lateinit var btnToInstructions: Button
    private lateinit var btnCancelRecipe: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter
    private lateinit var itemsArrayList: ArrayList<ItemsDataClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPreferences = SharedPreferencesHelper(this)

        addIngredientButton = findViewById(R.id.addIngredientButton)
        btnToInstructions = findViewById(R.id.btnToInstructions)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        recyclerView = findViewById(R.id.recyclerViewIngredients)
        itemsArrayList = arrayListOf()


        btnCancelRecipe.setOnClickListener {
            showCancelConfirmationDialog()
        }

        addIngredientButton.setOnClickListener {
            addIngredientRow()
        }

        slideToInstructionsPage()

        // Setup RecyclerView
        myAdapter = MyAdapter(itemsArrayList)

        setupRecyclerView()

        // Add some initial data for testing
        myAdapter.notifyDataSetChanged()
    }

    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            sharedPreferences.deleteData("videoLink")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
            finish()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = 0
        alertDialog.show()
    }

    private fun slideToInstructionsPage() {
        btnToInstructions.setOnClickListener {
            // Tüm değerleri topla ve SharedPreferences'e kaydet
            val ingredientText = collectEditTextValues()
            sharedPreferences.saveData("Ingredients", ingredientText)

            // Diğer işlemleri gerçekleştir
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }


    private fun collectEditTextValues(): String {
        val ingredientTextList = mutableListOf<String>()
        for (i in 0 until myAdapter.itemCount) {
            val itemView = recyclerView.findViewHolderForAdapterPosition(i)?.itemView
            val ingredientEditText = itemView?.findViewById<EditText>(R.id.ingredientEditText)

            val itemName = ingredientEditText?.text?.toString()?.trim()
            if (itemName != null && itemName.isNotBlank() && itemName != "New Ingredient") {
                ingredientTextList.add(itemName)
            } else {
                // Debugging statement to print the problematic values
                Log.d("IngredientDebug", "Skipping empty or 'New Ingredient' value: $itemName")
            }
        }
        return ingredientTextList.joinToString(", ")
    }





    private fun addIngredientRow() {
        val newItem = ItemsDataClass("New Ingredient")
        itemsArrayList.add(newItem)
        myAdapter.notifyItemInserted(itemsArrayList.size - 1)
    }

    private fun setupRecyclerView() {
        myAdapter = MyAdapter(itemsArrayList)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        // ... (existing setup code)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = source.adapterPosition
                val targetPosition = target.adapterPosition

                Collections.swap(itemsArrayList, sourcePosition, targetPosition)
                myAdapter.notifyItemMoved(sourcePosition, targetPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                itemsArrayList.removeAt(position)
                myAdapter.notifyItemRemoved(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // Limit the maximum swipe distance to half of the item width
                val maxSwipeDistance = viewHolder.itemView.width / 2 .toFloat()
                val limitedDX = if (dX < -maxSwipeDistance) -maxSwipeDistance else dX

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    limitedDX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            this@IngredientsActivity,
                            com.example.recipe.R.color.my_background
                        )
                    )
                    .addSwipeLeftCornerRadius(
                        TypedValue.COMPLEX_UNIT_DIP,
                        resources.getDimension(R.dimen.corner_radius)
                    )
                    .addSwipeRightCornerRadius(
                        TypedValue.COMPLEX_UNIT_DIP,
                        resources.getDimension(R.dimen.corner_radius)
                    )
                    .addActionIcon(com.example.recipe.R.drawable.baseline_delete)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, limitedDX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}
