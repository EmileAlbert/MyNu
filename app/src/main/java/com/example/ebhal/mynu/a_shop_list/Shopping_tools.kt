package com.example.ebhal.mynu.a_shop_list

import android.util.Log
import com.example.ebhal.mynu.data.Ingredient
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.data.Recipe

const val TAG = "shopping_list_tools"

fun makeShoppingList(list_pairRecipesGuests: List<Pair<Recipe, Int>>): MutableList<Item> {

    Log.i(TAG, "Make shopping list")

    val shoppingList = mutableListOf<Item>()
    val rawIngredientList = mutableListOf<Ingredient>()

    // Extract every ingredient in recipes ingredient lists and multiply by guest number
    for (pair in list_pairRecipesGuests){

        val guestNB = pair.second
        val ingredientsList = pair.first.ing_string2list(pair.first.ingredient_list)

        for (ingredient in ingredientsList){

            Log.i(TAG, "ingredient_multiply_quantity $ingredient")
            val list_value_decade_unit = ingredient.getList_ValueDecadeUnit()

            val mult_qty = java.lang.Float.valueOf(list_value_decade_unit[0])* guestNB.toFloat()

            ingredient.quantity = "$mult_qty-${list_value_decade_unit[1]}-${list_value_decade_unit[2]}"

            Log.i(TAG, "mult ingredient : $ingredient")
            rawIngredientList.add(ingredient)
        }
    }

    Log.i(TAG, "ingredient2item _ concat management")
    // Make shopping list made of items and concatenate item if already in shopping list
    for (ingredient in rawIngredientList){

        var double = false
        var item_index = 0

        for (item in shoppingList){

            // double in list - update existing item with concatenation
            if (ingredient.name == item.name){

                Log.i(TAG, "double : $item" )

                double = true
                shoppingList[item_index] = concat_ingredient_item(ingredient, item)
                break
            }

            item_index += 1
        }

        // no double in shopping list - add item
        if (!double) {
            shoppingList.add(Item(ingredient.name, ingredient.quantity))
        }
    }

    return shoppingList
}

fun concat_ingredient_item(ingredient: Ingredient, item: Item): Item {

    val ing_1 = ingredient
    val ing_2 = Ingredient(item.name, item.quantity)

    val concat_item = Item(item.name, "")

    // norm_qty form : value-decade-unit with unit = g or l or i
    val norm_qty_1 = ing_1.normalizedQuantityValue_fromNorm().split("-")
    val norm_qty_2 = ing_2.normalizedQuantityValue_fromNorm().split("-")

    Log.i(TAG, "concat value - value 1 : $norm_qty_1[0] - value 2 : $norm_qty_2[0]")

    val norm_added_value = java.lang.Float.valueOf(norm_qty_1[0]) + java.lang.Float.valueOf(norm_qty_2[0])

    // bring back ingredient quantity in decade (choosen as the highest between ing1 and ing2 decade)
    if (norm_qty_1[2] == "g"){

        val decade2factorMap = ing_1.decade2factor_g()
        var decade = norm_qty_1[1]

        if (decade2factorMap[norm_qty_1[1]]!! < decade2factorMap[norm_qty_1[2]]!!) {
            decade = norm_qty_1[2]
        }

        concat_item.quantity = ing_1.norm2decade(norm_added_value, decade, "g")
    }

    else if (norm_qty_1[2] == "l"){

        val decade2factorMap = ing_1.decade2factor_l()
        var decade = norm_qty_1[1]

        if (decade2factorMap[norm_qty_1[1]]!! < decade2factorMap[norm_qty_1[2]]!!) {
            decade = norm_qty_1[2]
        }

        concat_item.quantity = ing_1.norm2decade(norm_added_value, decade, "l")
    }

    else {
        concat_item.quantity = "$norm_added_value-i-i"
    }

    return concat_item
}

fun shoppingListIsCompleted(shopping_list : List<Item>) : Boolean {

    for (item in shopping_list){ if (!item.check){return false} }
    return true

}

fun add_item_first2list(item : Item, items_list : MutableList<Item>) : MutableList<Item> {

    val res = mutableListOf<Item>(item)
    res.addAll(items_list)
    return res
}



