import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.example.recipe.model.Category
import com.example.recipe.model.Recipe
import java.io.File

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "recipe.db"
        private const val DATABASE_VERSION = 2

        // Table names
        private const val TABLE_CATEGORIES = "categories"
        private const val TABLE_RECIPES = "recipes"

        // Common column names
        private const val COLUMN_ID = "id"

        // Categories table column names
        private const val COLUMN_CATEGORY_NAME = "categoryName"
        private const val COLUMN_CATEGORY_DESCRIPTION = "categoryDescription"
        private const val COLUMN_CATEGORY_PHOTO_PATH = "categoryPhotoPath"

        // Recipes table column names
        private const val COLUMN_RECIPE_NAME = "recipeName"
        private const val COLUMN_SELECTED_CATEGORY = "selectedCategory"
        private const val COLUMN_INGREDIENTS = "ingredients"
        private const val COLUMN_INSTRUCTIONS = "instructions"
        private const val COLUMN_TEMPERATURE = "temperature"
        private const val COLUMN_RECIPE_PHOTO_PATH = "recipePhotoPath"
        private const val COLUMN_RECIPE_RATE = "recipeRate"
        private const val COLUMN_VIDEO_LINK = "videoLink"
        private const val COLUMN_COOKING_TIME = "cookingTime"
        private const val COLUMN_PREPARATION_TIME = "preparationTime"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the "categories" table
        val createCategoriesTableQuery = "CREATE TABLE $TABLE_CATEGORIES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CATEGORY_NAME TEXT," +
                "$COLUMN_CATEGORY_DESCRIPTION TEXT," +
                "$COLUMN_CATEGORY_PHOTO_PATH TEXT" +
                ")"
        db.execSQL(createCategoriesTableQuery)

        // Create the "recipes" table
        val createRecipesTableQuery = "CREATE TABLE $TABLE_RECIPES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_RECIPE_NAME TEXT," +
                "$COLUMN_SELECTED_CATEGORY TEXT," +
                "$COLUMN_INGREDIENTS TEXT," +
                "$COLUMN_INSTRUCTIONS TEXT," +
                "$COLUMN_TEMPERATURE TEXT," +
                "$COLUMN_RECIPE_PHOTO_PATH TEXT," +
                "$COLUMN_RECIPE_RATE TEXT," +
                "$COLUMN_VIDEO_LINK TEXT," +
                "$COLUMN_COOKING_TIME TEXT," +
                "$COLUMN_PREPARATION_TIME TEXT" +
                ")"
        db.execSQL(createRecipesTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPES")

        // Create new tables
        onCreate(db)

    }

    // Helper methods for database operations



    // Insert a new category into the "categories" table
    fun insertCategory(
        categoryName: String,
        categoryDescription: String,
        categoryPhotoPath: String
    ): Long {
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, categoryName)
            put(COLUMN_CATEGORY_DESCRIPTION, categoryDescription)
            put(COLUMN_CATEGORY_PHOTO_PATH, categoryPhotoPath)
        }

        val db = writableDatabase
        val id = db.insert(TABLE_CATEGORIES, null, values)
        db.close()

        return id
    }

    fun deleteCategory(categoryId: Long): Boolean {
        val db = writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(categoryId.toString())
        val deletedRows = db.delete(TABLE_CATEGORIES, selection, selectionArgs)
        db.close()

        return deletedRows > 0
    }

    // Insert a new recipe into the "recipes" table
    fun insertRecipe(
        recipeName: String,
        selectedCategory: String,
        ingredients: String,
        instructions: String,
        temperature: String,
        recipePhotoPath: String,
        recipeRate: String,
        videoLink: String,
        cookingTime: String,
        preparationTime: String,
    ): Long {
        val values = ContentValues().apply {
            put(COLUMN_RECIPE_NAME, recipeName)
            put(COLUMN_SELECTED_CATEGORY, selectedCategory)
            put(COLUMN_INGREDIENTS, ingredients)
            put(COLUMN_INSTRUCTIONS, instructions)
            put(COLUMN_TEMPERATURE, temperature)
            put(COLUMN_RECIPE_PHOTO_PATH, recipePhotoPath)
            put(COLUMN_RECIPE_RATE, recipeRate)
            put(COLUMN_VIDEO_LINK, videoLink)
            put(COLUMN_COOKING_TIME, cookingTime)
            put(COLUMN_PREPARATION_TIME, preparationTime)
        }

        val db = writableDatabase
        val id = db.insert(TABLE_RECIPES, null, values)
        db.close()

        return id
    }

    fun deleteRecipe(recipeId: Long): Boolean {
        val db = writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(recipeId.toString())
        val deletedRows = db.delete(TABLE_RECIPES, selection, selectionArgs)
        db.close()

        return deletedRows > 0
    }


    private fun decodeBitmapFromFile(filePath: String): Bitmap? {
        return try {
            val file = File(filePath)
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Get all categories from the "categories" table
    fun getAllCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()
        val selectQuery = "SELECT * FROM $TABLE_CATEGORIES"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        val columnIndexId = cursor.getColumnIndex(COLUMN_ID)
        val columnIndexCategoryName = cursor.getColumnIndex(COLUMN_CATEGORY_NAME)
        val columnIndexCategoryDescription = cursor.getColumnIndex(COLUMN_CATEGORY_DESCRIPTION)
        val columnIndexCategoryPhotoPath = cursor.getColumnIndex(COLUMN_CATEGORY_PHOTO_PATH)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(columnIndexId)
                val categoryName = cursor.getString(columnIndexCategoryName)
                val categoryDescription = cursor.getString(columnIndexCategoryDescription)
                val categoryPhotoPath = cursor.getString(columnIndexCategoryPhotoPath) //We saved Photo_Path as string
                val bitmap = decodeBitmapFromFile(categoryPhotoPath)

                val category = Category(id, categoryName, categoryDescription, bitmap)
                categoryList.add(category)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return categoryList
    }

    fun getSpecificRecipes(categoryName: String): List<Recipe> {
        val recipeList = mutableListOf<Recipe>()

        val selectQuery = "SELECT * FROM $TABLE_RECIPES WHERE $COLUMN_SELECTED_CATEGORY = ?"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(categoryName))

        val recipeIndexId = cursor.getColumnIndex(COLUMN_ID)
        val recipeName = cursor.getColumnIndex(COLUMN_RECIPE_NAME)
        val recipeCategoryName = cursor.getColumnIndex(COLUMN_SELECTED_CATEGORY)
        val recipeIngredients = cursor.getColumnIndex(COLUMN_INGREDIENTS)
        val recipeInstructions = cursor.getColumnIndex(COLUMN_INSTRUCTIONS)
        val recipePhotoPath = cursor.getColumnIndex(COLUMN_RECIPE_PHOTO_PATH)
        val recipeTemperature = cursor.getColumnIndex(COLUMN_TEMPERATURE)
        val recipeRate = cursor.getColumnIndex(COLUMN_RECIPE_RATE)
        val videoLink = cursor.getColumnIndex(COLUMN_VIDEO_LINK)
        val cookingTime = cursor.getColumnIndex(COLUMN_COOKING_TIME)
        val preparationTime = cursor.getColumnIndex(COLUMN_PREPARATION_TIME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(recipeIndexId)
            val title = cursor.getString(recipeName)
            val categoryName = cursor.getString(recipeCategoryName)
            val ingredients = cursor.getString(recipeIngredients)
            val instructions = cursor.getString(recipeInstructions)
            val temperature = cursor.getString(recipeTemperature)

            val image = cursor.getString(recipePhotoPath)
            val bitmapImage = decodeBitmapFromFile(image)
            val recipeRate = cursor.getString(recipeRate)
            val videoLink = cursor.getString(videoLink)
            val cookingTime = cursor.getString(cookingTime)
            val preparationTime = cursor.getString(preparationTime)

            val recipe = Recipe(id, title, categoryName, ingredients, instructions, temperature, bitmapImage, recipeRate,videoLink, cookingTime, preparationTime)
            recipeList.add(recipe)
        }

        cursor.close()
        db.close()

        return recipeList
    }

    fun getRecipeBitmap(recipeName: String): Bitmap? {
        val selectQuery = "SELECT $COLUMN_RECIPE_PHOTO_PATH FROM $TABLE_RECIPES WHERE $COLUMN_RECIPE_NAME = ?"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(recipeName))

        val recipePhotoPath = cursor.getColumnIndex(COLUMN_RECIPE_PHOTO_PATH)

        var bitmap: Bitmap? = null

        if (cursor.moveToFirst()) {
            val photoPath = cursor.getString(recipePhotoPath)
            bitmap = decodeBitmapFromFile(photoPath)
        }

        cursor.close()
        db.close()

        return bitmap
    }

    fun getRecipeColumnData(recipeId: Long, columnName: String): String? {
        val selectQuery = "SELECT $columnName FROM $TABLE_RECIPES WHERE $COLUMN_ID = ?"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(recipeId.toString()))

        var columnData: String? = null

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(columnName)
            if (columnIndex != -1) {
                columnData = cursor.getString(columnIndex)
            }
        }

        cursor.close()
        db.close()

        return columnData
    }

    fun deleteRecipeColumnData(recipeId: Long, columnName: String): Boolean {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(recipeId.toString())

        return try {
            // Sütunu boş bir dizeyle güncellemek için ContentValues kullanma
            val contentValues = ContentValues()
            contentValues.put(columnName, "") // Set the column value to an empty string
            // Belirli sütunu güncelle
            db.update(TABLE_RECIPES, contentValues, whereClause, whereArgs) > 0
        } finally {
            db.close()
        }
    }

    fun updateRecipeColumnData(recipeId: Long, columnName: String, newValue: String): Boolean {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(recipeId.toString())

        return try {
            // Use ContentValues to specify the new value for the column
            val contentValues = ContentValues()
            contentValues.put(columnName, newValue)

            // Update the specified column with the new value
            db.update(TABLE_RECIPES, contentValues, whereClause, whereArgs) > 0
        } finally {
            db.close()
        }
    }
    fun showTableColumns() {
        val columnNames = mutableListOf<String>()
        val selectQuery = "PRAGMA table_info(recipes)"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor != null) {
            val columnNameIndex = cursor.getColumnIndex("name")
            if (columnNameIndex != -1) {
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(columnNameIndex)
                    columnNames.add(columnName)
                }
            }
            cursor.close()
        }
        db.close()

// Access the column names in the 'columnNames' list
        for (columnName in columnNames) {
            println(columnName)
        }

    }

}
