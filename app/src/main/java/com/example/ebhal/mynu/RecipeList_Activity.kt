package com.example.ebhal.mynu

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.support.v7.widget.Toolbar
import com.example.ebhal.mynu.R.styleable.FloatingActionButton
import com.example.ebhal.mynu.utils.deleteNote
import com.example.ebhal.mynu.utils.persistRecipe
import com.example.ebhal.mynu.utils.loadRecipes

// parentActivity of the application
class RecipeList_Activity : AppCompatActivity(), View.OnClickListener {

    // TODO understand lateinit
    lateinit var adapter: RecipeAdapter
    lateinit var recipes : MutableList<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list_)

        //recipes = mutableListOf<Recipe>()
        recipes = loadRecipes(this)
        adapter = RecipeAdapter(recipes, this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        findViewById<FloatingActionButton>(R.id.create_recipe_fab).setOnClickListener(this)

        val recyclerView = findViewById(R.id.recipes_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Test recipe creation
        recipes.add(Recipe("Test 1", 10, 3, 2, "Ingrédients 1 et 2","Recette", 3.14f, "Dinner", 2, true, false))

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
        // Option 1 : just a RESULT_OK code and there is nothing to do
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        // Option 2 : REQUEST_EDIT_RECIPE and we need to save the ew value for the recipe object
        when (requestCode) {
            RecipeDetail_Activity.REQUEST_EDIT_RECIPE -> processEditRecipeResult(data)
        }
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
