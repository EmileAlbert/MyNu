package com.example.ebhal.mynu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.ebhal.mynu.R.styleable.FloatingActionButton
import com.example.ebhal.mynu.utils.*
import java.io.BufferedReader
import java.io.InputStreamReader


// parentActivity of the application
class RecipeList_Activity : AppCompatActivity(), View.OnClickListener {

    // TODO understand lateinit
    lateinit var adapter: RecipeAdapter
    lateinit var recipes : MutableList<Recipe>

    val REQUEST_IMPORT = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list_)

        try{
            recipes = loadRecipes(this)
        }
        finally {}

        adapter = RecipeAdapter(recipes, this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        findViewById<FloatingActionButton>(R.id.create_recipe_fab).setOnClickListener(this)

        val recyclerView = findViewById(R.id.recipes_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Test recipe creation
        recipes.add(Recipe("Test 1", 10, 1.0f, 2.0f, "Ingrédients 1 et 2","Recette 1", 1.11f, "Dinner", 1, true, false))
        recipes.add(Recipe("Test 2", 20, 2.0f, 3.0f, "Ingrédients 3 et 4","Recette 2", 2.22f, "Dinner", 2, true, false))
        recipes.add(Recipe("Test 3", 30, 3.0f, 4.0f, "Ingrédients 5 et 6","Recette 3", 3.33f, "Dinner", 3, true, false))

    }

    // Add menu toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_recipes_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_import_recipe -> {
                val intent = Intent()
                        .setType("text/csv")
                        .setAction(Intent.ACTION_GET_CONTENT)

                startActivityForResult(Intent.createChooser(intent, "Select a file"),REQUEST_IMPORT)
                return true
            }

            R.id.action_export_recipe -> {
                val csv_object = CSV()
                csv_object.exportCSV(this, recipes)
                return true

            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Code executed when click on a card of the list
    override fun onClick(view : View) {
        if(view.tag != null){
            val recipe_index = view.tag as Int
            showRecipeDetail(recipe_index)
        }

        else{
            when(view.id){
                R.id.create_recipe_fab -> showRecipeDetail(-1)
            }
        }
    }

    // Code executed when return from child activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        val csv_object = CSV()

        // Option 1 : just a RESULT_OK code and there is nothing to do
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            // Option 2 : REQUEST_EDIT_RECIPE and we need to save the new value for the recipe object
            RecipeDetail_Activity.REQUEST_EDIT_RECIPE -> processEditRecipeResult(data)

            // Option 3 : REQUEST_IMPORT and we need to read imported CSV file
            REQUEST_IMPORT -> import_recipes(csv_object.import(this, contentResolver.openInputStream(data.data)))
        }
    }

    // Add imported recipes to recipes list if does not exist
    fun import_recipes(import : MutableList<Recipe>) {
        var existing_recipes_name : MutableList<String> = mutableListOf()
        for (recipe in recipes){existing_recipes_name.add(recipe.name)}
        for (imported_recipe in import) {

            if (imported_recipe.name !in existing_recipes_name) {recipes.add(imported_recipe)}
        }

        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Import réussi", Toast.LENGTH_LONG).show()
    }

    // Create the new recipe object or delete it function of the action contained in the Intent
    private fun processEditRecipeResult(data: Intent){
        val recipe_index = data.getIntExtra(RecipeDetail_Activity.EXTRA_RECIPE_INDEX, -1)

        when (data.action) {
            RecipeDetail_Activity.ACTION_SAVE_RECIPE -> {
                val recipe = data.getParcelableExtra<Recipe>(RecipeDetail_Activity.EXTRA_RECIPE)
                saveRecipe(recipe, recipe_index)
            }

            RecipeDetail_Activity.ACTION_DELETE_RECIPE -> {
                deleteRecipe(recipe_index)
            }
        }
    }

    // Save new recipe in recipes list and notify adapter
    fun saveRecipe(recipe: Recipe, recipe_index: Int) {
        persistRecipe(this, recipe)
        if (recipe_index < 0) {recipes.add(0, recipe)}
        else {recipes[recipe_index] = recipe}

        adapter.notifyDataSetChanged()
    }

    // Delete recipe from recipes list and notify adapter
    private fun deleteRecipe(recipe_index: Int) {
        if(recipe_index < 0){return}

        val recipe = recipes.removeAt(recipe_index)
        deleteNote(this, recipe)
        adapter.notifyDataSetChanged()

    }

    // Launch RecipeDetail_Activity activity
    fun showRecipeDetail(recipe_index : Int){
        val recipe = if(recipe_index < 0) Recipe() else recipes[recipe_index]

        //Toast.makeText(this, "Recette sélectionnée : ${recipe.title}", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, RecipeDetail_Activity::class.java)
        intent.putExtra(RecipeDetail_Activity.EXTRA_RECIPE, recipe as Parcelable)
        intent.putExtra(RecipeDetail_Activity.EXTRA_RECIPE_INDEX, recipe_index)
        startActivityForResult(intent, RecipeDetail_Activity.REQUEST_EDIT_RECIPE)
    }
}
