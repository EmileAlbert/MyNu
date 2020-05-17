package com.example.ebhal.mynu.utils

import android.util.Log
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.data.Recipe

private val TAG = "storage"
const val fake_item_name = "fake_item_fix_sl%"

// RECIPE DATABASE MANAGEMENT /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Write note in memory
fun persistRecipe_database(database : Database, recipe: Recipe) : Boolean {
    Log.i(TAG, "Persist recipe")
    val result: Boolean

    // recipe is not in database (new one)
    if (recipe.id < 0){
        result = database.create_recipe(recipe)
    }

    // recipe already in database (existing one)
    else {
        result = database.update_recipe(recipe)
    }

    return result
}

// Load all the recipes from persistent memory
fun loadRecipes_database(database: Database): MutableList<Recipe>{

    return database.get_recipes()
}

// Suppress one recipe
fun deleteRecipe_database(database : Database, recipe_ID : Long) : Boolean {

    val result = database.delete_recipe(recipe_ID)
    return result
}


// EXTRA RECIPE DATABASE MANAGEMENT ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun insertExtraRecipe_database(database: Database, recipe : Recipe) : Boolean {

    return database.add_extra_recipe(recipe.id.toInt())
}

fun deleteExtraRecipe_database(database: Database, recipe: Recipe) : Boolean {

    return database.delete_extra_recipe(recipe.id.toInt())
}

fun updateExtraRecipe(database : Database, old_recipe : Recipe, new_recipe : Recipe) : Boolean {

    return database.update_extra_recipe(old_recipe.id.toInt(), new_recipe.id.toInt())
}

fun setExtraRecipeGuestNB_database(database: Database, recipe_ID : Long, guest_nb: Int) : Boolean {

    return database.update_guest_number_extra(recipe_ID.toInt(), guest_nb)
}

fun getExtraRecipeList_database(database: Database) : MutableList<Recipe>{

    val extra_recipe_ID = database.get_extra_recipes_ID()
    val main_recipes_list = database.get_recipes()

    var extra_recipes_list = mutableListOf<Recipe>()

    for (recipe_ID in extra_recipe_ID){

        for (recipe in main_recipes_list){

            if (recipe.id.toInt() == recipe_ID){

                extra_recipes_list.add(recipe)
                break
            }
        }
    }

    return extra_recipes_list
}

fun getExtraGuestNumber_database(database: Database) : MutableList<Int> {

    return database.get_extra_recipe_guestNB()
}

fun resetExtraRecipe_database(database: Database) {

    database.delete_all_extra_recipe()
}


// MENU DATABASE MANAGEMENT ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun persistDayMenu_database(database: Database, day_index : Int, recipe_id : Int) : Boolean{

    Log.i(TAG, "Persist menu assignation $day_index - $recipe_id")

    return database.update_dayMenu(day_index, recipe_id)
}

fun persistDayGuest_database(database: Database, day_index: Int, guest_nb: Int) : Boolean{

    Log.i(TAG, "Persist guest assignation $day_index - $guest_nb")

    return database.update_dayGuest(day_index, guest_nb)
}

fun resetMenu_database(database: Database){

    Log.i(TAG, "Reset week menu")
    database.reset_menu()
}

fun loadMenu_database(database : Database, empty_recipe : Recipe) : MutableList<Recipe> {

    val menu_list_recipe = mutableListOf<Recipe>()
    val menu_list_IDs = mutableListOf<Int>()

    val main_recipes_list = loadRecipes_database(database)
    val week_recipe_IDs = database.get_menu_index()

    for (recipe_ID in week_recipe_IDs){

        if (recipe_ID < 0){
            menu_list_recipe.add(empty_recipe)
            menu_list_IDs.add(-1)
        }

        else {
            menu_list_recipe.add(getRecipeFromID(recipe_ID, main_recipes_list, empty_recipe))
            menu_list_IDs.add(recipe_ID)
        }
    }

    Log.i(TAG, "Load menu : $menu_list_IDs + $menu_list_recipe")
    return menu_list_recipe
}

fun getRecipeFromID(recipe_ID : Int, main_recipes_list : MutableList<Recipe>, empty_recipe : Recipe) : Recipe {

    var res_recipe = empty_recipe

    for (recipe in main_recipes_list){

        if (recipe.id.toInt() == recipe_ID){ res_recipe = recipe }
    }

    return res_recipe
}


// SHOPPING LIST DATABASE MANAGEMENT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun shoppingListIsEmpty_database(database: Database) : Boolean{

    return database.get_items_count() <= 0
}

fun saveShoppingList_database(database: Database, shop_list : List<Item>) : Boolean{

    var res = true

    for (item in shop_list){
        if(!database.create_item(item)){res = false}
    }

    return res
}

fun loadShoppingList_database(database: Database): MutableList<Item> {

    val shopping_list = database.get_items()
    return shopping_list

}

fun updateShoppingList_database(database: Database, shopping_list: List<Item>) : Boolean {

    var res = true

    for (item in shopping_list){

        // New item to add in db
        if (item.dbID.toInt() == -1){
            if(!database.create_item(item)){res = false}
            Log.i(TAG, "updateShoppingList_database : new item $res")
        }

        else {
            if(!database.update_item(item)){res = false}
            Log.i(TAG, "updateShoppingList_database : existing item $res")
        }
    }

    return res
}

fun emptyShoppingList_database(database: Database){

    database.delete_all_items()
}

fun fixedShoppingList_database(database : Database, shopping_started : Boolean) : Boolean {

    val fake_item = Item(fake_item_name, "", -1, true, true)

    if (shopping_started){database.create_item(fake_item)}

    else {

        val item_list = database.get_items()

        for (item in item_list){
            if (item.name == fake_item_name){
                database.delete_item(item.dbID)
            }
        }
    }

    return true
}

// return true if an item is checked
fun checkeditem_database(database : Database) : Boolean {

    val items_list = database.get_items()

    for (item in items_list){if (item.check){return true}}

    return false
}