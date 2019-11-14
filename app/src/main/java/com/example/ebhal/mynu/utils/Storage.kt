package com.example.ebhal.mynu.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.example.ebhal.mynu.Recipe
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

private val TAG = "storage"

fun persistRecipe(context: Context, recipe: Recipe) {

    if (TextUtils.isEmpty(recipe.filename)){
        recipe.filename = UUID.randomUUID().toString() + ".recipe"
    }
    Log.i(TAG, "Save Recipe")
    val fileOutput = context.openFileOutput(recipe.filename, Context.MODE_PRIVATE)
    val outputStream = ObjectOutputStream(fileOutput)
    outputStream.writeObject(recipe)
    outputStream.close()
}

fun loadRecipes(context: Context): MutableList<Recipe>{
    val recipes = mutableListOf<Recipe>()

    val recipesDir = context.filesDir
    Log.i(TAG, "Load recipes 1")
    for(filename in recipesDir.list()){
        val recipe = loadRecipe(context, filename)
        recipes.add(recipe)
    }

    Log.i(TAG, "Load recipes 2")
    return recipes
}

fun deleteNote(context: Context, recipe: Recipe){
    context.deleteFile(recipe.filename)
}

private fun loadRecipe(context: Context, filename: String) : Recipe {
    Log.i(TAG, "Load recipe 1")
    val fileInput = context.openFileInput(filename)
    val inputStream = ObjectInputStream(fileInput)
    val recipe = inputStream.readObject() as Recipe
    inputStream.close()
    Log.i(TAG, "Load recipe 2")
    return recipe
}


