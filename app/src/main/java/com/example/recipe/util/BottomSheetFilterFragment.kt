import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.recipe.R
import com.example.recipe.activity.viewrecipe.ViewRecipeActivity
import com.example.recipe.enum.Cuisine
import com.example.recipe.enum.Diet
import com.example.recipe.enum.Nutrition
import com.example.recipe.model.Recipe
import com.example.recipe.util.FilterViewModel
import com.example.recipe.util.FlowLayout
import com.example.recipe.util.SharedPreferencesHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class BottomSheetFilterFragment() : BottomSheetDialogFragment() {


    //binding
    private lateinit var scrollView: ScrollView
    private lateinit var addTagContainer: LinearLayout
    private lateinit var removeTagContainer: LinearLayout
    private lateinit var addTagEditText: EditText
    private lateinit var removeTagEditText: EditText
    private lateinit var btnAddIngredientTag: Button
    private lateinit var btnRemoveIngredientTag: Button
    private lateinit var addContainer: FlowLayout
    private lateinit var removeContainer: FlowLayout
    private lateinit var steppedSeekBar: SeekBar
    private lateinit var steppedSeekBarPrep: SeekBar
    private lateinit var steppedSeekBarRating: SeekBar
    private lateinit var selectedTimeTextView: TextView
    private lateinit var selectedPrepTextView: TextView
    private lateinit var selectedTimeTextViewRating: TextView
    private lateinit var viewRecipeActivity: ViewRecipeActivity
    private lateinit var chipGroupDiet: ChipGroup
    private lateinit var chipGroupNutrition: ChipGroup
    private lateinit var chipGroupCuisine: ChipGroup
    private lateinit var removeFilter: Button
    private lateinit var filterViewModel: FilterViewModel

    private var isAtTop: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter, container, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.FilterLayout)
        val layoutParams = linearLayout.layoutParams as? FrameLayout.LayoutParams
            ?: LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        filterViewModel = ViewModelProvider(requireActivity()).get(FilterViewModel::class.java)

        val addedTags = filterViewModel.addedTags
        val removedTags = filterViewModel.removedTags
        val selectedChips = filterViewModel.selectedChips


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
        chipGroupDiet = view.findViewById(R.id.chipGroupDiet)
        chipGroupNutrition = view.findViewById(R.id.chipGroupNutrition)
        chipGroupCuisine = view.findViewById(R.id.chipGroupCuisine)
        removeFilter = view.findViewById(R.id.btnRemoveFilter)
        steppedSeekBarPrep = view.findViewById(R.id.steppedSeekBarPrep)
        selectedPrepTextView = view.findViewById(R.id.selectedPrepTextView)
        scrollView = view.findViewById(R.id.scrollView)
        steppedSeekBar = view.findViewById(R.id.steppedSeekBar)
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView)

        steppedSeekBarRating = view.findViewById(R.id.steppedSeekBarRating)
        selectedTimeTextViewRating = view.findViewById(R.id.selectedTimeTextViewRating)





        btnAddIngredientTag.setOnClickListener {
            addTag(addTagEditText, addTagContainer, addContainer, addToAddedTags = true, addedTags, removedTags)
        }


        btnRemoveIngredientTag.setOnClickListener {
            addTag(removeTagEditText, removeTagContainer, removeContainer, addToAddedTags = false,addedTags,removedTags)
        }

        removeFilter.setOnClickListener {
            clearAllFilters(addedTags, removedTags)
        }

        setupSteppedSeekBarTime()
        setupSteppedSeekBarPrep()
        setupSteppedSeekBarRating()

        setupChipGroup(chipGroupDiet, Diet.getDiets(), selectedChips)
        setupChipGroup(chipGroupNutrition, Nutrition.getNutritions(),selectedChips)
        setupChipGroup(chipGroupCuisine, Cuisine.getCuisines(),selectedChips)
        // Add a scroll listener to the ScrollView
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            // Check if the top of the filterLayout is at the top of the ScrollView
            isAtTop = linearLayout.top == scrollView.scrollY

            // Enable or disable scrolling based on the position
            scrollView.isScrollContainer = isAtTop

            // Set the behavior to expand only if at the top
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.isDraggable = isAtTop
        }

        setTagsToContainer(addTagContainer, addContainer, addedTags)
        setTagsToContainer(removeTagContainer, removeContainer, removedTags)
        //set chips

        val applyButton = view.findViewById<Button>(R.id.btnApplyFilter)
        applyButton.setOnClickListener {
            applyFilters(addedTags, removedTags, selectedChips)
            println("\nUsing forEach:")
            addedTags.forEach { tag ->
                println(tag)
            }
            // Get the BottomSheetBehavior from the parent view
            val parentView = requireView().parent as View
            val bottomSheetBehavior = BottomSheetBehavior.from(parentView)

            // Set the state to STATE_HIDDEN
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        return view
    }


    //Set the previous tags when fragment is created
    private fun setTagsToContainer(
        tagContainer: LinearLayout,
        tagTextContainer: FlowLayout,
        tags: MutableSet<String>
    ) {
        tags.forEach { tagText ->
            val tagView = layoutInflater.inflate(R.layout.item_filter_tag, tagContainer, false)
            val tagTextTextView = tagView.findViewById<TextView>(R.id.tagText)
            val crossIconImageView = tagView.findViewById<ImageView>(R.id.crossIcon)

            tagTextTextView.text = tagText

            // Set OnClickListener for cross icon to remove the tag
            crossIconImageView.setOnClickListener {
                tagTextContainer.removeView(tagView)
                // Remove the tag from the corresponding set (addedTags or removedTags)
                tags.remove(tagText)
            }

            // Add the tag view below the corresponding EditText
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 8, 0, 0)  // Adjust margin as needed
            tagTextContainer.addView(tagView, params)
        }
    }
    private fun clearAllFilters(addedTags:MutableSet<String>, removedTags:MutableSet<String>) {
        // Clear added and removed tags
        addedTags.clear()
        removedTags.clear()

        //Clear the containers
        clearTagContainer(addContainer, addedTags)
        clearTagContainer(removeContainer, removedTags)

        // Clear cooking time and rating
        steppedSeekBar.progress = 0
        steppedSeekBarPrep.progress = 0
        steppedSeekBarRating.progress = 0
        selectedTimeTextView.text = getString(R.string.selected_time, 0)
        selectedTimeTextViewRating.text = getString(R.string.selected_rating, 1)

        // Clear ChipGroups
        clearChipGroup(chipGroupDiet)
        clearChipGroup(chipGroupNutrition)
        clearChipGroup(chipGroupCuisine)

        // Leave tag containers and associated EditText widgets as they are

        // Notify ViewRecipeActivity to reset the recipes
        viewRecipeActivity.resetRecipes()
    }

    private fun clearTagContainer(
        tagTextContainer: FlowLayout,
        tags: MutableSet<String>
    ) {
        // Clear tags from the set
        tags.clear()

        // Remove all views from the tagTextContainer
        tagTextContainer.removeAllViews()
    }

    private fun clearChipGroup(chipGroup: ChipGroup) {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isChecked = false
        }
    }

    private fun mapCookingTimeValue(value: Int): Int {
        // Assuming the received values are in the range 1-2-3
        // Map them to corresponding bar values: 0, 15, 30
        return when (value) {
            1 -> 0
            2 -> 15
            3 -> 30
            4 -> 45
            5 -> 60
            6 -> 75
            else -> 0 // Handle any other values as needed
        }
    }
    private fun applyFilters(addedTags:MutableSet<String>, removedTags:MutableSet<String>, selectedChips:MutableSet<String>) {
        val cookingTime = mapCookingTimeValue(steppedSeekBar.progress + 1)
        val prepTime = mapCookingTimeValue(steppedSeekBarPrep.progress+1)
        val rating = steppedSeekBarRating.progress + 1

        println("Cooking Time: $cookingTime Rating: $rating")

        // Örneğin chip gruplarını kaydet
        val selectedChipsDiet = getSelectedChips(chipGroupDiet)
        val selectedChipsNutrition = getSelectedChips(chipGroupNutrition)
        val selectedChipsCuisine = getSelectedChips(chipGroupCuisine)

        selectedChips += selectedChipsDiet
        selectedChips += selectedChipsNutrition
        selectedChips += selectedChipsCuisine

        val filteredRecipes = filterRecipes(
            viewRecipeActivity.originalRecipeList,
            addedTags,
            removedTags,
            cookingTime,
            prepTime,
            rating,
            selectedChipsDiet,
            selectedChipsNutrition,
            selectedChipsCuisine
        )

        // Filtrelenmiş reçeteleri ViewRecipeActivity'e bildir
        viewRecipeActivity.setRecipes(filteredRecipes)
    }
    private fun getSelectedChips(chipGroup: ChipGroup): Set<String> {
        val selectedChips = mutableSetOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedChips.add(chip.text.toString())
            }
        }
        return selectedChips
    }

    private fun filterRecipes(
        recipes: List<Recipe>,
        addedTags: Set<String>,
        removedTags: Set<String>,
        selectedCookingTime: Int,
        selectedPrepTime: Int,
        selectedRating: Int,
        selectedChipsDiet: Set<String>,
        selectedChipsNutrition: Set<String>,
        selectedChipsCuisine: Set<String>
    ): List<Recipe> {
        return recipes.filter { recipe ->
            // Check tags
            val recipeTags = recipe.ingredients.split(",").map { it.trim() }
            val hasAllAddedTags = recipeTags.containsAll(addedTags)
            val hasNoRemovedTags = removedTags.none { removedTag -> recipeTags.contains(removedTag) }

            // Check if the recipe contains selected chips in its tags
            val recipeChips = recipe.tags.split(",").map {it.trim()}
            val hasAllSelectedChipsDiet = recipeChips.containsAll(selectedChipsDiet)
            val hasAllSelectedChipsNutrition = recipeChips.containsAll(selectedChipsNutrition)
            val hasAllSelectedChipsCuisine = recipeChips.containsAll(selectedChipsCuisine)

            // Check cooking time and rating
            val recipeCookingTime = parseCookingTime(recipe.cookingTime)
            val recipePrepTime = parseCookingTime(recipe.prepTime)
            val recipeRating = recipe.rating.toFloat()

            // Ignore rating filter if the recipe has a rating of 1
            // Ignore cooking time filter if the recipe has a cooking time of 0
            val validRating = recipeRating >= selectedRating
            val validTime = recipeCookingTime >= selectedCookingTime
            val validPrep = recipePrepTime >= selectedPrepTime

            // Filter recipes based on all criteria
            (hasAllAddedTags && hasNoRemovedTags) &&
                    validTime && validPrep && validRating &&
                    hasAllSelectedChipsDiet && hasAllSelectedChipsNutrition && hasAllSelectedChipsCuisine
        }
    }


    private fun parseCookingTime(cookingTime: String): Int {
        // Assuming cookingTime is in the format "HH:mm"
        val parts = cookingTime.split(":")
        if (parts.size == 2) {
            val hours = parts[0].toIntOrNull() ?: 0
            val minutes = parts[1].toIntOrNull() ?: 0
            return hours * 60 + minutes // Convert to total minutes
        }
        return 0 // Return 0 for invalid format or null cooking time
    }

    private fun saveStringSetData(key: String, value: MutableSet<String>) {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        sharedPreferencesHelper.saveStringSetData(key, value)
    }

    private fun saveChipGroupData(key: String, vararg chipGroups: ChipGroup) {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val selectedChips = mutableSetOf<String>()

        for (chipGroup in chipGroups) {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    // Chip is checked, save its text
                    selectedChips.add(chip.text.toString())
                }
            }
        }

        // Save the set of selected chips as a comma-separated string
        sharedPreferencesHelper.saveStringData(key, selectedChips.joinToString(","))
    }

    override fun onStart() {
        super.onStart()

        // Set the behavior to expand only if at the top
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.isDraggable = isAtTop
            bottomSheet.setBackgroundResource(R.drawable.bottom_sheet_background)
        }
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

    private fun setupChipGroup(chipGroup: ChipGroup, enums: List<Enum<*>>, initiallySelectedChips: MutableSet<String>) {
        val checkedChips = mutableSetOf<Int>()

        for (enumValue in enums) {
            val chip = Chip(chipGroup.context)
            if (enumValue is DisplayNameProvider) {
                chip.text = (enumValue as DisplayNameProvider).displayName
            } else {
                chip.text = enumValue.name
            }
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { _, isChecked ->
                val position = chipGroup.indexOfChild(chip)

                if (isChecked) {
                    // Chip is checked, save its position
                    checkedChips.add(position)
                } else {
                    // Chip is unchecked, remove its position
                    checkedChips.remove(position)

                    // Remove the unchecked chip from initiallySelectedChips
                    initiallySelectedChips.remove(chip.text.toString())
                }
            }
            chipGroup.addView(chip)

            // Check the chip if it is initially selected
            if (initiallySelectedChips.contains(chip.text.toString())) {
                chip.isChecked = true
            }
        }

        // Restore the state of checked chips when the group is initialized
        for (position in checkedChips) {
            val chip = chipGroup.getChildAt(position) as Chip
            chip.isChecked = true
        }
    }




    // Add this interface to enums that provide a display name
    interface DisplayNameProvider {
        val displayName: String
    }

    private fun setupSteppedSeekBarTime() {
        // Set initial text
        val initialMinutes = steppedSeekBar.progress * 15
        selectedTimeTextView.text = getString(R.string.selected_time, initialMinutes)

        steppedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change
                val selectedMinutes = progress * 15  // 0, 15, 30, 45, 60, 75, 90 minutes
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

    private fun setupSteppedSeekBarPrep() {
        // Set initial text
        val initialMinutes = steppedSeekBar.progress * 15
        selectedPrepTextView.text = getString(R.string.selected_time, initialMinutes)

        steppedSeekBarPrep.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change
                val selectedMinutes = progress * 15  // 0, 15, 30, 45, 60, 75, 90 minutes
                selectedPrepTextView.text = getString(R.string.selected_time, selectedMinutes)
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

    private fun addTag(
        EditText: EditText,
        tagContainer: LinearLayout,
        tagTextContainer: FlowLayout,
        addToAddedTags: Boolean,
        addedTags:MutableSet<String>, removedTags:MutableSet<String>
    ): String? {
        val tagText = EditText.text.toString().trim()

        if (tagText.isNotEmpty()) {
            val tagView = layoutInflater.inflate(R.layout.item_filter_tag, tagContainer, false)
            val tagTextTextView = tagView.findViewById<TextView>(R.id.tagText)
            val crossIconImageView = tagView.findViewById<ImageView>(R.id.crossIcon)

            if (addToAddedTags) {
                addedTags.add(tagText)
            } else {
                removedTags.add(tagText)
            }

            tagTextTextView.text = tagText

            // Set OnClickListener for cross icon to remove the tag
            crossIconImageView.setOnClickListener {
                tagTextContainer.removeView(tagView)
                if (addToAddedTags) {
                    addedTags.remove(tagText)
                } else {
                    removedTags.remove(tagText)
                }
            }

            // Add the tag view below the corresponding EditText
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 8, 0, 0)  // Adjust margin as needed
            tagTextContainer.addView(tagView, params)

            // Clear the edit text
            EditText.text.clear()

            return tagText
        }

        return null
    }


}
