package com.example.ebhal.mynu.utils

import android.util.Log
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.data.Recipe

private val TAG = "storage"

// RECIPE DATABASE MANAGEMENT /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Write note in memory
fun persistRecipe_database(database : Database, recipe: Recipe) : Boolean {
    Log.i(TAG, "Persist recipe")
    var result = false

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

    var result = database.delete_recipe(recipe_ID)
    return result
}


// MENU DATABASE MANAGEMENT ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun persistDayMenu_database(database: Database, day_index : Int, recipe_list_index : Int) : Boolean{

    Log.i(TAG, "Persist menu assignation $day_index - $recipe_list_index")

    return database.update_dayMenu(day_index, recipe_list_index)
}

fun persistDayGuest_database(database: Database, day_index: Int, guest_nb: Int) : Boolean{

    Log.i(TAG, "Persist guest assignation $day_index - $guest_nb")

    return database.update_dayGuest(day_index, guest_nb)
}

fun resetMenu_database(database: Database){

    Log.i(TAG, "Reset week menu")
    database.reset_menu()
}

fun loadMenu_database(database : Database, empty_recipe : Recipe) : Pair<MutableList<Recipe>, MutableList<Int>> {

    var menu_list_recipe = mutableListOf<Recipe>()
    var menu_list_index = mutableListOf<Int>()

    var main_recipes_list = loadRecipes_database(database)
    var menu_recipe_index = database.get_menu_index()

    for (recipe_index in menu_recipe_index){

        if (recipe_index < 0){
            menu_list_recipe.add(empty_recipe)
            menu_list_index.add(-1)
        }

        else {
            menu_list_recipe.add(main_recipes_list[recipe_index])
            menu_list_index.add(recipe_index)
        }
    }

    Log.i(TAG, "Load menu : $menu_list_index + $menu_list_recipe")
    return Pair(menu_list_recipe, menu_list_index)
}


// SHOPPING LIST DATABASE MANAGEMENT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// DATABASE MANAGEMENT ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    var shopping_list = database.get_items()
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



