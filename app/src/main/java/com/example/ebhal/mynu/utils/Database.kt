package com.example.ebhal.mynu.utils

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.data.Recipe

private const val DATABASE_NAME = "recipe.db"
private const val DATABASE_VERSION = 1

private const val RECIPE_TABLE_NAME = "recipe"
private const val MENU_TABLE_NAME = "menu"
private const val SHOPLIST_TABLE_NAME = "shopping_list"

// Database columns name for recipe table
private const val RECIPE_KEY_ID = "id"
private const val RECIPE_KEY_NAME = "name"
private const val RECIPE_KEY_DURATION = "duration"
private const val RECIPE_KEY_PRICE = "price"
private const val RECIPE_KEY_EASE = "ease"
private const val RECIPE_KEY_SCORE = "score"
private const val RECIPE_KEY_INGREDIENTS = "ingredients"
private const val RECIPE_KEY_STEPS = "steps"
private const val RECIPE_KEY_MEAL = "meal"
private const val RECIPE_KEY_NSCORE = "nutriscore"
private const val RECIPE_KEY_VEGGIE = "veggie"
private const val RECIPE_KEY_SALTY = "salty"
private const val RECIPE_KEY_TEMP = "temperature"
private const val RECIPE_KEY_SEASON = "season"
private const val RECIPE_KEY_ORDINARY = "ordinary"
private const val RECIPE_KEY_ORIGINALITY = "originality"

// Database columns name for menu table
private const val MENU_KEY_ID = "id"
private const val MENU_KEY_DAY = "day_index"
private const val MENU_KEY_RECIPE_IDX = "recipe_index"
private const val MENU_KEY_GUEST_NB = "guest_number"

// Database columns name for shopping list table
private const val SHOPLIST_KEY_ID = "id"
private const val SHOPLIST_KEY_ITEM_NAME = "item_name"
private const val SHOPLIST_KEY_ITEM_QTY  = "item_qty"
private const val SHOPLIST_KEY_ITEM_RCPOS = "item_rc_position"
private const val SHOPLIST_KEY_ITEM_CHECK  = "item_check"
private const val SHOPLIST_KEY_INDEPENDENCE = "item_independence"


// Database table creation request
private const val RECIPE_TABLE_CREATE_REQUEST = "CREATE TABLE $RECIPE_TABLE_NAME ($RECIPE_KEY_ID INTEGER PRIMARY KEY, " +
                                                                                 "$RECIPE_KEY_NAME TEXT, " +
                                                                                 "$RECIPE_KEY_DURATION INTEGER, " +
                                                                                 "$RECIPE_KEY_PRICE REAL," +
                                                                                 "$RECIPE_KEY_EASE REAL, " +
                                                                                 "$RECIPE_KEY_SCORE REAL, " +
                                                                                 "$RECIPE_KEY_INGREDIENTS TEXT, " +
                                                                                 "$RECIPE_KEY_STEPS TEXT, " +
                                                                                 "$RECIPE_KEY_MEAL TEXT, " +
                                                                                 "$RECIPE_KEY_NSCORE INTEGER, " +
                                                                                 "$RECIPE_KEY_VEGGIE TEXT, " +
                                                                                 "$RECIPE_KEY_SALTY TEXT, " +
                                                                                 "$RECIPE_KEY_TEMP TEXT, " +
                                                                                 "$RECIPE_KEY_SEASON TEXT, " +
                                                                                 "$RECIPE_KEY_ORDINARY TEXT, " +
                                                                                 "$RECIPE_KEY_ORIGINALITY TEXT)"

private const val MENU_TABLE_CREATE_REQUEST = "CREATE TABLE $MENU_TABLE_NAME ($MENU_KEY_ID INTEGER PRIMARY KEY, " +
                                                                             "$MENU_KEY_DAY INTEGER, " +
                                                                             "$MENU_KEY_RECIPE_IDX INTEGER, " +
                                                                             "$MENU_KEY_GUEST_NB INTEGER)"

private const val SHOPLIST_TABLE_CREATE_REQUEST = "CREATE TABLE $SHOPLIST_TABLE_NAME ($SHOPLIST_KEY_ID INTEGER PRIMARY KEY, " +
                                                                                     "$SHOPLIST_KEY_ITEM_NAME TEXT, " +
                                                                                     "$SHOPLIST_KEY_ITEM_QTY TEXT, " +
                                                                                     "$SHOPLIST_KEY_ITEM_RCPOS INTEGER," +
                                                                                     "$SHOPLIST_KEY_ITEM_CHECK TEXT," +
                                                                                     "$SHOPLIST_KEY_INDEPENDENCE TEXT)"


class Database(context: Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    val TAG = Database::class.java.simpleName

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(RECIPE_TABLE_CREATE_REQUEST)
        db?.execSQL(MENU_TABLE_CREATE_REQUEST)
        db?.execSQL(SHOPLIST_TABLE_CREATE_REQUEST)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        // To change body of created functions use File | Settings | File Templates.
    }

    // RECIPE DATABASE MANAGEMENT /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun create_recipe(recipe: Recipe) : Boolean {

        val values = ContentValues()

        values.put(RECIPE_KEY_NAME, recipe.name)
        values.put(RECIPE_KEY_DURATION, recipe.duration)
        values.put(RECIPE_KEY_PRICE, recipe.price)
        values.put(RECIPE_KEY_EASE, recipe.ease)
        values.put(RECIPE_KEY_SCORE, recipe.score)
        values.put(RECIPE_KEY_INGREDIENTS, recipe.ingredient_list)
        values.put(RECIPE_KEY_STEPS, recipe.recipe_steps)
        values.put(RECIPE_KEY_MEAL, recipe.meal_time)
        values.put(RECIPE_KEY_NSCORE, recipe.nutri_score)
        values.put(RECIPE_KEY_VEGGIE, recipe.veggie.toString())
        values.put(RECIPE_KEY_SALTY, recipe.salty.toString())
        values.put(RECIPE_KEY_TEMP, recipe.temp.toString())
        values.put(RECIPE_KEY_SEASON, recipe.season.toString())
        values.put(RECIPE_KEY_ORDINARY, recipe.ordinary.toString())
        values.put(RECIPE_KEY_ORIGINALITY, recipe.original.toString())

        Log.d(TAG, "Create recipe $values")

        val id = writableDatabase.insert(RECIPE_TABLE_NAME, null, values)
        recipe.id = id

        return id > 0

    }

    fun update_recipe(recipe: Recipe) : Boolean{

        var result = false
        val values = ContentValues()

        values.put(RECIPE_KEY_NAME, recipe.name)
        values.put(RECIPE_KEY_DURATION, recipe.duration)
        values.put(RECIPE_KEY_PRICE, recipe.price)
        values.put(RECIPE_KEY_EASE, recipe.ease)
        values.put(RECIPE_KEY_SCORE, recipe.score)
        values.put(RECIPE_KEY_INGREDIENTS, recipe.ingredient_list)
        values.put(RECIPE_KEY_STEPS, recipe.recipe_steps)
        values.put(RECIPE_KEY_MEAL, recipe.meal_time)
        values.put(RECIPE_KEY_NSCORE, recipe.nutri_score)
        values.put(RECIPE_KEY_VEGGIE, recipe.veggie.toString())
        values.put(RECIPE_KEY_SALTY, recipe.salty.toString())
        values.put(RECIPE_KEY_TEMP, recipe.temp.toString())
        values.put(RECIPE_KEY_SEASON, recipe.season.toString())
        values.put(RECIPE_KEY_ORDINARY, recipe.ordinary.toString())
        values.put(RECIPE_KEY_ORIGINALITY, recipe.original.toString())

        Log.d(TAG, "Update recipe $values")

        val recipe_ID = recipe.id
        try {
            writableDatabase.update(RECIPE_TABLE_NAME, values, "$RECIPE_KEY_ID = $recipe_ID", null)
            result = true
        }

        catch (e : Exception){}

        return result
    }

    fun delete_recipe(recipe_ID : Long) : Boolean {

        var result = false

        Log.d(TAG, "Delete recipe $recipe_ID")

        try {
            writableDatabase.delete(RECIPE_TABLE_NAME, "$RECIPE_KEY_ID = $recipe_ID", null)
            result = true
        }

        catch (e : Exception){}

        return result
    }

    fun get_recipes() : MutableList<Recipe> {

        var recipe_list = mutableListOf<Recipe>()
        readableDatabase.rawQuery("SELECT * FROM $RECIPE_TABLE_NAME", null).use { cursor ->
            while (cursor.moveToNext()) {

                val name = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_NAME))
                val duration = cursor.getInt(cursor.getColumnIndex(RECIPE_KEY_DURATION))
                val ease = cursor.getFloat(cursor.getColumnIndex(RECIPE_KEY_EASE))
                val score = cursor.getFloat(cursor.getColumnIndex(RECIPE_KEY_SCORE))
                val ingredients = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_INGREDIENTS))
                val steps = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_STEPS))
                val price = cursor.getFloat(cursor.getColumnIndex(RECIPE_KEY_PRICE))
                val meal = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_MEAL))
                val nutriscore = cursor.getInt(cursor.getColumnIndex(RECIPE_KEY_NSCORE))
                val veggie = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_VEGGIE))
                val salty = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_SALTY))
                val temp = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_TEMP))
                val season = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_SEASON))
                val ordinary = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_ORDINARY))
                val originality = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_ORIGINALITY))
                val id = cursor.getLong(cursor.getColumnIndex(RECIPE_KEY_ID))

                var recipe = Recipe(name, duration, ease, score, ingredients, steps, price, meal, nutriscore,
                        veggie.toBoolean(), salty.toBoolean(), temp.toBoolean(), season.toBoolean(),
                        ordinary.toBoolean(), originality.toBoolean(), id)

                recipe_list.add(recipe)
            }

        }
            return recipe_list
    }

    fun get_recipe_count() : Int = DatabaseUtils.queryNumEntries(readableDatabase, RECIPE_TABLE_NAME, null).toInt()


    // MENU DATABASE MANAGEMENT ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun get_menu_index() : MutableList<Int> {
        var menu_index = mutableListOf<Int>(-1, -1, -1, -1, -1, -1, -1)

        try {
            readableDatabase.rawQuery("SELECT * FROM $MENU_TABLE_NAME", null).use { cursor ->
                while (cursor.moveToNext()) {

                    val day = cursor.getInt(cursor.getColumnIndex(MENU_KEY_DAY))
                    val recipe_index = cursor.getInt(cursor.getColumnIndex(MENU_KEY_RECIPE_IDX))

                    menu_index[day] = recipe_index
                }
            }

            Log.d(TAG, "get_menu_index : success : $menu_index")
            return menu_index

        }

        catch(e : Exception) {
            Log.d(TAG, "get_menu_index : failed : $e")
            return menu_index
        }
    }

    fun get_daysGuest() : MutableList<Int> {

        var days_guest = mutableListOf<Int>(-1, -1, -1, -1, -1, -1, -1)

        try {
            readableDatabase.rawQuery("SELECT * FROM $MENU_TABLE_NAME", null).use { cursor ->
                while (cursor.moveToNext()) {

                    val day = cursor.getInt(cursor.getColumnIndex(MENU_KEY_DAY))
                    val guest_nb = cursor.getInt(cursor.getColumnIndex(MENU_KEY_GUEST_NB))

                    days_guest[day] = guest_nb
                }
            }

            Log.d(TAG, "get_days_guestNB : success : $days_guest")
            return days_guest

        }

        catch(e : Exception) {
            Log.d(TAG, "get_days_guestNB : failed : $e")
            return days_guest
        }
    }

    fun update_dayMenu(day_index : Int, recipe_list_index : Int) : Boolean {
        var result = false

        Log.d(TAG, "Update menu day $day_index - $recipe_list_index")

        try {

            writableDatabase.execSQL("UPDATE $MENU_TABLE_NAME SET $MENU_KEY_RECIPE_IDX = $recipe_list_index WHERE $MENU_KEY_DAY = $day_index;")
            result = true
        }

        catch (e : Exception){
            Log.d(TAG, "Update dayMenu failed : $e")
        }

        return result
    }

    fun update_dayGuest(day_index: Int, guest_number : Int) : Boolean {
        var result = false

        Log.d(TAG, "Update guest number for day $day_index - $guest_number")

        try {

            writableDatabase.execSQL("UPDATE $MENU_TABLE_NAME SET $MENU_KEY_GUEST_NB = $guest_number WHERE $MENU_KEY_DAY = $day_index;")
            result = true
        }

        catch (e : Exception){
            Log.d(TAG, "Update day guest failed : $e")
        }

        return result
    }

    fun reset_menu() : Boolean{

        Log.d(TAG, "Reset week menu")

        if (get_menuDays_count() < 6){
            Log.d(TAG, "db table menu not initialized - count : ${get_menuDays_count()}")
            initialized_menu_table()

        }

        else {
            Log.d(TAG, "db table menu initialized - count : ${get_menuDays_count()}")

            var day_list = mutableListOf<Int>(0, 1, 2, 3, 4, 5, 6)

            for (day in day_list){
                update_dayMenu(day, -1)
                update_dayGuest(day, 2)
            }
        }

        return true
    }

    fun get_menuDays_count() : Int = DatabaseUtils.queryNumEntries(readableDatabase, MENU_TABLE_NAME, null).toInt()

    fun initialized_menu_table(){

        Log.d(TAG, "Menu table init")

        val week_days = listOf<Int>(0, 1, 2, 3, 4, 5, 6)

        for (day in week_days){

            val values = ContentValues()

            values.put(MENU_KEY_DAY, day)
            values.put(MENU_KEY_RECIPE_IDX, -1)
            values.put(MENU_KEY_GUEST_NB, 2)

            writableDatabase.insert(MENU_TABLE_NAME, null, values)
        }
    }


    // SHOPPING LIST DATABASE MANAGEMENT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun create_item(item : Item) : Boolean{

        val values = ContentValues()

        values.put(SHOPLIST_KEY_ITEM_NAME, item.name)
        values.put(SHOPLIST_KEY_ITEM_QTY, item.quantity)
        values.put(SHOPLIST_KEY_ITEM_RCPOS, item.rc_position)
        values.put(SHOPLIST_KEY_ITEM_CHECK, item.check.toString())
        values.put(SHOPLIST_KEY_INDEPENDENCE, item.independence.toString())


        Log.i(TAG, "Create item $values")

        var id = writableDatabase.insert(SHOPLIST_TABLE_NAME, null, values)

        item.dbID = id
        Log.i(TAG, "ID created item : ${item.dbID}")
        return id > 0
    }

    fun update_item(item : Item) : Boolean{

        var result = false
        val values = ContentValues()

        values.put(SHOPLIST_KEY_ITEM_NAME, item.name)
        values.put(SHOPLIST_KEY_ITEM_QTY, item.quantity)
        values.put(SHOPLIST_KEY_ITEM_RCPOS, item.rc_position)
        values.put(SHOPLIST_KEY_ITEM_CHECK, item.check.toString())
        values.put(SHOPLIST_KEY_INDEPENDENCE, item.independence.toString())

        Log.i(TAG, "Update item $values")

        val item_ID = item.dbID

        try {
            writableDatabase.update(SHOPLIST_TABLE_NAME, values, "$SHOPLIST_KEY_ID = $item_ID", null)
            result = true
            Log.i(TAG, "Success item")
        }

        catch (e : Exception){
            Log.i(TAG, "Failed udpate item : $e")
        }

        return result
    }

    fun delete_item(item_ID : Long) : Boolean {

        var result = false

        Log.d(TAG, "Delete recipe $item_ID")

        try {
            writableDatabase.delete(SHOPLIST_TABLE_NAME, "$SHOPLIST_KEY_ID = $item_ID", null)
            result = true
        }

        catch (e : Exception){}

        return result
    }

    fun get_items() : MutableList<Item> {

        var items_list = mutableListOf<Item>()
        readableDatabase.rawQuery("SELECT * FROM $SHOPLIST_TABLE_NAME", null).use { cursor ->
            while (cursor.moveToNext()) {

                val name = cursor.getString(cursor.getColumnIndex(SHOPLIST_KEY_ITEM_NAME))
                val quantity = cursor.getString(cursor.getColumnIndex(SHOPLIST_KEY_ITEM_QTY))
                val rc_position = cursor.getInt(cursor.getColumnIndex(SHOPLIST_KEY_ITEM_RCPOS))
                val check = cursor.getString(cursor.getColumnIndex(SHOPLIST_KEY_ITEM_CHECK))
                val independence = cursor.getString(cursor.getColumnIndex(SHOPLIST_KEY_INDEPENDENCE))
                val id = cursor.getLong(cursor.getColumnIndex(SHOPLIST_KEY_ID))

                val item = Item(name, quantity,rc_position, check!!.toBoolean(), independence!!.toBoolean() ,id)

                items_list.add(item)
            }
        }

        return items_list
    }

    fun get_items_count() : Int = DatabaseUtils.queryNumEntries(readableDatabase, SHOPLIST_TABLE_NAME, null).toInt()

    fun delete_all_items() {
        writableDatabase.execSQL("DELETE FROM $SHOPLIST_TABLE_NAME")
    }
}