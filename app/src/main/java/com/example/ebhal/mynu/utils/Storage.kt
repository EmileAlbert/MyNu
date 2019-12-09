package com.example.ebhal.mynu.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.example.ebhal.mynu.Recipe
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

private val TAG = "storage"

// Write note in memory
fun persistRecipe(context: Context, recipe: Recipe) {

    // Create the unique title of the note if doesn't exist
    if (TextUtils.isEmpty(recipe.filename)){
        recipe.filename = UUID.randomUUID().toString() + ".recipe"}
    Log.i(TAG, "Save Recipe")

    val fileOutput = context.openFileOutput(recipe.filename, Context.MODE_PRIVATE)
    val outputStream = ObjectOutputStream(fileOutput)
    outputStream.writeObject(recipe)
    outputStream.close()
}

// Load all the recipes
fun loadRecipes(context: Context): MutableList<Recipe>{

    val recipes = mutableListOf<Recipe>()

    val recipesDir = context.filesDir
    Log.i(TAG, "Load recipes list")
    for(filename in recipesDir.list()){
        val recipe = loadRecipe(context, filename)
        recipes.add(recipe)
    }

    return recipes
}

// Suppress one note
fun deleteNote(context: Context, recipe: Recipe){
    context.deleteFile(recipe.filename)
}

// Load recipe
private fun loadRecipe(context: Context, filename: String) : Recipe {
    val fileInput = context.openFileInput(filename)
    val inputStream = ObjectInputStream(fileInput)
    val recipe = inputStream.readObject() as Recipe
    inputStream.close()
    return recipe
}




