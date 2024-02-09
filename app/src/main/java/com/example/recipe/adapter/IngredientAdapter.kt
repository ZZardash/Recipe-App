import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe.R
import com.example.recipe.models.Ingredient

// ... (existing imports)

class IngredientAdapter(
    private val itemsList: List<Ingredient>,
    private val textChangeListener: IngredientTextChangeListener
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val etIngredient: EditText = itemView.findViewById(R.id.etIngredient)
        private val etQuantity: EditText = itemView.findViewById(R.id.etQuantity)
        private val unitSpinner: Spinner = itemView.findViewById(R.id.spUnit)

        fun bind(item: Ingredient, position: Int) {
            etIngredient.setText(item.name)
            etQuantity.setText(item.quantity)

            // Implement TextWatcher to listen for changes in the Ingredient etIngredient
            etIngredient.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Notify the activity about the changed text
                    textChangeListener.onIngredientTextChanged(position, s?.toString() ?: "")
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Implement TextWatcher to listen for changes in the Quantity etIngredient
            etQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // You may want to handle quantity changes here if needed
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Set a listener for the Unit Spinner if needed
            unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // You may want to handle unit selection changes here if needed
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected in the spinner
                }
            }
        }
    }

    interface IngredientTextChangeListener {
        fun onIngredientTextChanged(position: Int, newText: String)
    }
}
