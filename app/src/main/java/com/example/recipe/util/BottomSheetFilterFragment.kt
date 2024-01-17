package com.example.recipe.util

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.example.recipe.R
import com.example.recipe.activity.ViewRecipeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFilterFragment : BottomSheetDialogFragment() {

    private lateinit var addTagContainer: LinearLayout
    private lateinit var removeTagContainer: LinearLayout
    private lateinit var addTagEditText: EditText
    private lateinit var removeTagEditText: EditText
    private lateinit var btnAddIngredientTag: Button
    private lateinit var btnRemoveIngredientTag: Button
    private lateinit var addContainer: LinearLayout
    private lateinit var removeContainer: LinearLayout
    private lateinit var steppedSeekBar: SeekBar
    private lateinit var steppedSeekBarRating: SeekBar
    private lateinit var selectedTimeTextView: TextView
    private lateinit var selectedTimeTextViewRating: TextView
    private lateinit var viewRecipeActivity: ViewRecipeActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter, container, false)

        val linearLayout = view.findViewById<LinearLayout>(R.id.FilterLayout)
        val layoutParams = linearLayout.layoutParams as? LinearLayout.LayoutParams
            ?: LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        viewRecipeActivity = activity as ViewRecipeActivity

        layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.fixed_margin)
        layoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.fixed_margin)
        linearLayout.layoutParams = layoutParams


        addTagContainer = view.findViewById(R.id.addTagContainer)
        removeTagContainer = view.findViewById(R.id.removeTagContainer)
        addTagEditText = view.findViewById(R.id.etAddIngredientTag)
        removeTagEditText = view.findViewById(R.id.etRemoveIngredientTag)
        btnAddIngredientTag = view.findViewById(R.id.btnAddIngredientTag)
        btnRemoveIngredientTag = view.findViewById(R.id.btnRemoveIngredientTag)
        addContainer = view.findViewById(R.id.addContainer)
        removeContainer = view.findViewById(R.id.removeContainer)

        steppedSeekBar = view.findViewById(R.id.steppedSeekBar)
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView)

        steppedSeekBarRating = view.findViewById(R.id.steppedSeekBarRating)
        selectedTimeTextViewRating = view.findViewById(R.id.selectedTimeTextViewRating)

        btnAddIngredientTag.setOnClickListener {
            addTag(addTagEditText, addTagContainer, addContainer)
        }

        btnRemoveIngredientTag.setOnClickListener {
            addTag(removeTagEditText, removeTagContainer, removeContainer)
        }


        setupSteppedSeekBarTime()
        setupSteppedSeekBarRating()

        return view

        return view
    }
    private fun setupSteppedSeekBarTime() {
        // Set initial text
        val initialMinutes = (steppedSeekBar.progress + 1) * 15
        selectedTimeTextView.text = getString(R.string.selected_time, initialMinutes)

        steppedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change
                val selectedMinutes = (progress + 1) * 15  // 15, 30, 45, 60, 75, 90 minutes
                selectedTimeTextView.text = getString(R.string.selected_time, selectedMinutes)
                // Do something with the selectedMinutes
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Handle tracking touch start
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Handle tracking touch stop
            }
        })
    }

    private fun setupSteppedSeekBarRating() {
        // Set initial text
        val initialRating = steppedSeekBarRating.progress + 1
        selectedTimeTextViewRating.text = getString(R.string.selected_rating, initialRating)

        steppedSeekBarRating.max = 4 // Since it's a 5-point scale, max value should be 4

        steppedSeekBarRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change
                val selectedRating = progress + 1
                selectedTimeTextViewRating.text = getString(R.string.selected_rating, selectedRating)
                // Do something with the selectedRating
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Handle tracking touch start
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Handle tracking touch stop
            }
        })
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


    private fun addTag(editText: EditText, tagContainer: LinearLayout, tagTextContainer: LinearLayout) {
        val tagText = editText.text.toString().trim()

        if (tagText.isNotEmpty()) {
            val tagView = layoutInflater.inflate(R.layout.item_filter_tag, tagContainer, false)
            val tagTextTextView = tagView.findViewById<TextView>(R.id.tagText)
            val crossIconImageView = tagView.findViewById<ImageView>(R.id.crossIcon)

            tagTextTextView.text = tagText

            // Set OnClickListener for cross icon to remove the tag
            crossIconImageView.setOnClickListener {
                tagTextContainer.removeView(tagView)
            }

            // Add the tag view below the corresponding EditText
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 8, 0, 0)  // Adjust margin as needed
            tagTextContainer.addView(tagView, params)

            // Clear the edit text
            editText.text.clear()
        }
    }

}

