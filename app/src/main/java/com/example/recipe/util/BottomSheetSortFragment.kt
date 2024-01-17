package com.example.recipe.util

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.example.recipe.R
import com.example.recipe.activity.ViewRecipeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetSortFragment : BottomSheetDialogFragment() {

    private lateinit var checkBoxLowRating: CheckBox
    private lateinit var checkBoxHighRating: CheckBox
    private lateinit var checkBoxLowIngredient: CheckBox
    private lateinit var checkBoxHighIngredient: CheckBox
    private lateinit var checkBoxLowTime: CheckBox
    private lateinit var checkBoxHighTime: CheckBox
    private lateinit var applyButton: Button
    private lateinit var viewRecipeActivity: ViewRecipeActivity

    private val checkBoxList: List<CheckBox> by lazy {
        listOf(
            checkBoxLowRating,
            checkBoxHighRating,
            checkBoxLowIngredient,
            checkBoxHighIngredient,
            checkBoxLowTime,
            checkBoxHighTime,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_sort, container, false)

        val linearLayout = view.findViewById<LinearLayout>(R.id.SortLayout)

        val layoutParams = linearLayout.layoutParams as? LinearLayout.LayoutParams
            ?: LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        viewRecipeActivity = activity as ViewRecipeActivity

        layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.fixed_margin)
        layoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.fixed_margin)
        linearLayout.layoutParams = layoutParams

        checkBoxLowRating = view.findViewById(R.id.checkBoxLowRating)
        checkBoxHighRating = view.findViewById(R.id.checkBoxHighRating)
        checkBoxLowIngredient = view.findViewById(R.id.checkBoxLowIngredient)
        checkBoxHighIngredient = view.findViewById(R.id.checkBoxHighIngredient)
        checkBoxLowTime = view.findViewById(R.id.checkBoxLowTime)
        checkBoxHighTime = view.findViewById(R.id.checkBoxHighTime)


        val textViewLowRating = view.findViewById<TextView>(R.id.textViewLowRating)
        val textViewHighRating = view.findViewById<TextView>(R.id.textViewHighRating)
        val textViewLowIngredient = view.findViewById<TextView>(R.id.textViewLowIngredient)
        val textViewHighIngredient = view.findViewById<TextView>(R.id.textViewHighIngredient)
        val textViewLowTime = view.findViewById<TextView>(R.id.textViewLowTime)
        val textViewHighTime = view.findViewById<TextView>(R.id.textViewHighTime)

        textViewLowRating.setOnClickListener { onTextViewClick(it) }
        textViewHighRating.setOnClickListener { onTextViewClick(it) }
        textViewLowIngredient.setOnClickListener { onTextViewClick(it) }
        textViewHighIngredient.setOnClickListener { onTextViewClick(it) }
        textViewLowTime.setOnClickListener { onTextViewClick(it) }
        textViewHighTime.setOnClickListener { onTextViewClick(it) }

        setupCheckBoxes()

        // apply button'ını tanımla
        applyButton = view.findViewById(R.id.btnApplySort)

        // applyButton'a OnClickListener ekle
        applyButton.setOnClickListener {
            // applySorting fonksiyonunu çalıştır
            applySorting(it)
        }


        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.isDraggable = true
            bottomSheet.setBackgroundResource(R.drawable.bottom_sheet_background)
        }
        return dialog
    }

    private fun setupCheckBoxes() {
        for (checkBox in checkBoxList) {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Only the checked CheckBox should remain checked, others should be unchecked
                    checkBoxList.filterNot { it == checkBox }.forEach { it.isChecked = false }
                }
            }

            // Set OnClickListener for associated TextView
            val textViewId: Int = when (checkBox) {
                checkBoxLowRating -> R.id.textViewLowRating
                checkBoxHighRating -> R.id.textViewHighRating
                checkBoxLowIngredient -> R.id.textViewLowIngredient
                checkBoxHighIngredient -> R.id.textViewHighIngredient
                checkBoxLowTime -> R.id.textViewLowTime
                checkBoxHighTime -> R.id.textViewHighTime
                else -> -1
            }

            val textView: TextView? = view?.findViewById(textViewId)
            textView?.setOnClickListener {
                // Toggle the state of the associated CheckBox
                checkBox.isChecked = !checkBox.isChecked
                // Uncheck other CheckBoxes
                checkBoxList.filterNot { it == checkBox }.forEach { it.isChecked = false }
            }
        }
    }

    fun onTextViewClick(view: View) {
        if (view is TextView) {
            val associatedCheckBox: CheckBox? = when (view.id) {
                R.id.textViewLowRating -> checkBoxLowRating
                R.id.textViewHighRating -> checkBoxHighRating
                R.id.textViewLowIngredient -> checkBoxLowIngredient
                R.id.textViewHighIngredient -> checkBoxHighIngredient
                R.id.textViewLowTime -> checkBoxLowTime
                R.id.textViewHighTime -> checkBoxHighTime
                else -> null
            }

            // Toggle the state of the associated CheckBox
            associatedCheckBox?.isChecked = !associatedCheckBox?.isChecked!!

            // Uncheck other CheckBoxes
            checkBoxList.filterNot { it == associatedCheckBox }.forEach { it.isChecked = false }
        }
    }



    fun applySorting(view: View) {
        // Implement sorting logic based on the selected option
        val sortedRecipes = when {
            checkBoxLowRating.isChecked -> viewRecipeActivity.recipeList.sortedBy { it.rating }
            checkBoxHighRating.isChecked -> viewRecipeActivity.recipeList.sortedByDescending { it.rating }
            checkBoxLowIngredient.isChecked -> viewRecipeActivity.recipeList.sortedBy { it.ingredients.split(",").size }
            checkBoxHighIngredient.isChecked -> viewRecipeActivity.recipeList.sortedByDescending { it.ingredients.split(",").size }
            checkBoxLowTime.isChecked -> viewRecipeActivity.recipeList.sortedBy { it.cookingTime + it.prepTime }
            checkBoxHighTime.isChecked -> viewRecipeActivity.recipeList.sortedByDescending { it.cookingTime + it.prepTime }
            else -> viewRecipeActivity.recipeList
        }


        // Update the RecyclerView accordingly
        viewRecipeActivity.recipeList.clear()
        viewRecipeActivity.recipeList.addAll(sortedRecipes)
        viewRecipeActivity.recipeAdapter.notifyDataSetChanged()

        // Close the bottom sheet dialog
        dismiss()
    }

}
