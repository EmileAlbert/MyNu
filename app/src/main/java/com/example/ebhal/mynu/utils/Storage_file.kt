package com.example.ebhal.mynu.utils

private val TAG = "storage"

// Write note in memory
//fun persistRecipe(context: Context, recipe: Recipe) {
//    // Create the unique title of the note if doesn't exist
//    if (TextUtils.isEmpty(recipe.filename)){
//        recipe.filename = UUID.randomUUID().toString() + ".recipe"
//    }
//    Log.i(TAG, "Persist recipe")
//
//    val fileOutput = context.openFileOutput(recipe.filename, Context.MODE_PRIVATE)
//    val outputStream = ObjectOutputStream(fileOutput)
//    outputStream.writeObject(recipe)
//    outputStream.close()
//}

// Load all the recipes from persistent memory
//fun loadRecipes(context: Context): MutableList<Recipe>{
//    val recipes = mutableListOf<Recipe>()
//
//    val recipesDir = context.filesDir
//    for(filename in recipesDir.list()){
//        val recipe = loadRecipe(context, filename)
//        recipes.add(recipe)
//    }
//
//    return recipes
//}

// Load 1 recipe
//private fun loadRecipe(context: Context, filename: String) : Recipe {
//    val fileInput = context.openFileInput(filename)
//    val inputStream = ObjectInputStream(fileInput)
//    val recipe = inputStream.readObject() as Recipe
//    inputStream.close()
//    return recipe
//}

// Suppress one recipe
//fun deleteRecipe(context: Context, recipe: Recipe){
//    context.deleteFile(recipe.filename)
//}





