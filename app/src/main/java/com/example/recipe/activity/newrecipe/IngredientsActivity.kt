package com.example.recipe.activity.newrecipe

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.activity.home.MainActivity
import com.example.recipe.adapter.IngredientAdapter
import com.example.recipe.adapter.IngredientUnitAdapter
import com.example.recipe.enum.IngredientQuantityUnit
import com.example.recipe.model.Ingredient
import com.example.recipe.util.SharedPreferencesHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.*

class IngredientsActivity : AppCompatActivity(), IngredientAdapter.IngredientTextChangeListener, IngredientAdapter.QuantityTextChangeListener {

    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var addIngredientButton: Button
    private lateinit var btnToInstructions: Button
    private lateinit var btnCancelRecipe: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var itemsArrayList: ArrayList<Ingredient>

    val ingredientTextList = mutableListOf<String>()
    val quantityTextList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // SharedPreferencesHelper sınıfı ile veri paylaşımı için nesne oluşturuluyor.
        sharedPreferences = SharedPreferencesHelper(this)

        // Arayüz bileşenlerine erişim için gerekli nesneler tanımlanıyor.
        addIngredientButton = findViewById(R.id.addIngredientButton)
        btnToInstructions = findViewById(R.id.btnToInstructions)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        recyclerView = findViewById(R.id.recyclerViewIngredients)
        itemsArrayList = arrayListOf()

        //Set the spinner
        //setupUnitSpinner()

        // İptal işlemi için onaylama dialogu gösteren fonksiyon çağrılıyor.
        btnCancelRecipe.setOnClickListener {
            showCancelConfirmationDialog()
        }

        // Yeni bir malzeme eklemek için butona tıklanınca çalışacak fonksiyon çağrılıyor.
        addIngredientButton.setOnClickListener {
            addIngredientRow()
        }

        // Tarif sayfasına geçiş yapacak butona tıklanınca çalışacak fonksiyon çağrılıyor.
        slideToInstructionsPage()

        // RecyclerView için gerekli ayarlamalar yapılıyor.
        setupRecyclerView()
    }

    private fun checkLastIngredientAndDisableButton() {
        // Enable the button if the last ingredient is not empty, otherwise disable it
        val lastIngredient = ingredientTextList.lastOrNull() ?: ""
        val lastQuantity = quantityTextList.lastOrNull() ?: ""

        // Enable the button if the last ingredient and quantity are not empty, otherwise disable it
        addIngredientButton.isEnabled = !lastIngredient.isBlank() && !lastQuantity.isBlank()
    }

    // İptal onaylama dialogunu gösteren fonksiyon
    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Video bağlantısını SharedPreferences'ten sil.
            sharedPreferences.deleteData("videoLink")

            // Ana aktiviteye geçiş yapılıyor.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Ekran geçiş animasyonları ayarlanıyor.
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


    // Tarif sayfasına geçiş yapacak butona tıklanınca çalışacak fonksiyon
    private fun slideToInstructionsPage() {
        btnToInstructions.setOnClickListener {
            // Tüm değerleri topla ve SharedPreferences'e kaydet
            val ingredientText = collectEditTextValues()
            sharedPreferences.saveClassListData("Ingredients", ingredientText)

            println("Ingredients: "+ingredientText)
            // Diğer işlemleri gerçekleştir
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)

            // Ekran geçiş animasyonları ayarlanıyor.
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    // EditText değerlerini toplayan ve birleştiren fonksiyon
    private fun collectEditTextValues(): List<Ingredient> {
        val ingredientList = mutableListOf<Ingredient>()
        for (i in 0 until ingredientAdapter.itemCount) {
            val itemView = recyclerView.findViewHolderForAdapterPosition(i)?.itemView
            val ingredientEditText = itemView?.findViewById<EditText>(R.id.etIngredient)
            val quantityEditText = itemView?.findViewById<EditText>(R.id.etQuantity)
            val unitSpinner = itemView?.findViewById<Spinner>(R.id.spUnit)

            val itemName = ingredientEditText?.text?.toString()?.trim()
            val quantity = quantityEditText?.text?.toString()?.trim() ?: ""
            val unit = unitSpinner?.selectedItem?.toString() ?: ""

            if (itemName != null && itemName.isNotBlank() && itemName != "") {
                ingredientList.add(Ingredient(itemName, quantity, unit))
            } else {
                // Handle empty or 'New Ingredient' value
                Log.d("IngredientDebug", "Skipping empty or 'New Ingredient' value: $itemName")
            }
        }
        checkLastIngredientAndDisableButton()
        return ingredientList
    }


    // Yeni bir malzeme satırı ekleyen fonksiyon
    private fun addIngredientRow() {
        // Add the new item to the adapter's dataset
        val newItem = Ingredient("")
        itemsArrayList.add(newItem)
        ingredientTextList.add("") // Add an empty string for the new ingredient
        quantityTextList.add("")
        ingredientAdapter.notifyItemInserted(itemsArrayList.size - 1)

        // Check if the last ingredient is empty and disable the button if true
        checkLastIngredientAndDisableButton()

        // Get the RecyclerView's layout manager
        val layoutManager = recyclerView.layoutManager

        // Check if the layout manager is LinearLayoutManager
        if (layoutManager is LinearLayoutManager) {
            // Scroll to the newly added item
            recyclerView.scrollToPosition(itemsArrayList.size - 1)
        }
    }

    // RecyclerView'i ayarlayan fonksiyon
    private fun setupRecyclerView() {
        ingredientAdapter = IngredientAdapter(itemsArrayList, this,this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ingredientAdapter

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
                ingredientAdapter.notifyItemMoved(sourcePosition, targetPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // Check if it's the first item and prevent deletion
                if (position == 0) {
                    ingredientAdapter.notifyItemChanged(position) // Notify to reset the swipe state
                    return
                }

                // Remove the item from both lists
                itemsArrayList.removeAt(position)
                ingredientTextList.removeAt(position)
                quantityTextList.removeAt(position)

                ingredientAdapter.notifyItemRemoved(position)

                // Check the last ingredient after deletion
                checkLastIngredientAndDisableButton()
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
                // Maksimum kaydırma mesafesini item genişliğinin yarısı olarak sınırla
                val maxSwipeDistance = viewHolder.itemView.width / 2.toFloat()
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
                            R.color.my_background
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
                    .addActionIcon(R.drawable.baseline_delete)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, limitedDX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onIngredientTextChanged(position: Int, newText: String) {
        if (position < ingredientTextList.size) {
            // If the position is within the bounds of the current list, update the element
            ingredientTextList[position] = newText
        } else if (position == ingredientTextList.size) {
            // If the position is equal to the current size, add a new element to the list
            ingredientTextList.add(newText)
        } else {
            // Handle the case where position is greater than the current size (should not happen)
            Log.e("IngredientDebug", "Invalid position: $position")
        }

        checkLastIngredientAndDisableButton()
    }

    override fun onQuantityTextChanged(position: Int, newText: String) {
        if (position < quantityTextList.size) {
            // If the position is within the bounds of the current list, update the element
            quantityTextList[position] = newText
        } else if (position == quantityTextList.size) {
            // If the position is equal to the current size, add a new element to the list
            quantityTextList.add(newText)
        } else {
            // Handle the case where position is greater than the current size (should not happen)
            Log.e("QuantityDebug", "Invalid position: $position")
        }

        checkLastIngredientAndDisableButton()
    }


}
