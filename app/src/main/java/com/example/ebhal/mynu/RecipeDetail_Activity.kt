package com.example.ebhal.mynu

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView

class RecipeDetail_Activity : AppCompatActivity() {

    companion object {
        val REQUEST_EDIT_RECIPE = 1
        val EXTRA_RECIPE = "recipe"
        val EXTRA_RECIPE_INDEX = "recipe_index"

        val ACTION_SAVE_RECIPE = "com.example.ebhal.mynu.actions.ACTION_SAVE_RECIPE"
        val ACTION_DELETE_RECIPE = "com.example.ebhal.mynu.actions.ACTION_DELETE_RECIPE"
    }

    lateinit var recipe : Recipe
    var recipe_index : Int = -1

    lateinit var  name_View : TextView
    lateinit var  duration_View : TextView
    // TODO declare ease (rating bar) widget
    // TODO declare score (rating bar) widget
    lateinit var  ingredients_View : TextView
    lateinit var  steps_View : TextView
    lateinit var  meal_View : TextView
    lateinit var  price_View : TextView
    lateinit var  nutriscore_View : TextView
    // TODO declare meat (button) widget
    // TODO declare health (button) widget


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar !!.setDisplayHomeAsUpEnabled(true)

        // Get recipe object
        recipe = intent.getParcelableExtra<Recipe>(EXTRA_RECIPE)
        recipe_index = intent.getIntExtra(EXTRA_RECIPE_INDEX, -1)

        // Get entries name
        name_View = findViewById(R.id.name) as TextView
        duration_View = findViewById(R.id.duration) as TextView
        // TODO handle ease (rating bar) widget
        // TODO handle score (rating bar) widget
        ingredients_View = findViewById(R.id.ingredients) as TextView
        steps_View = findViewById(R.id.steps) as TextView
        price_View = findViewById(R.id.price) as TextView
        meal_View = findViewById(R.id.meal) as TextView
        nutriscore_View = findViewById(R.id.nutriscore) as TextView
        // TODO handle meat (button) widget
        // TODO handle health (button) widget

        // Fill entries with existing value of the selected recipe if existing
        name_View.text = recipe.name
        duration_View.text = recipe.duration.toString()
        // TODO handle ease (rating bar) widget
        // TODO handle score (rating bar) widget
        ingredients_View.text = recipe.ingredient_list
        steps_View.text = recipe.recipe_steps
        price_View.text = recipe.price.toString()
        meal_View.text = recipe.meal_time
        nutriscore_View.text = recipe.nutri_score.toString()
        // TODO handle meat (button) widget
        // TODO handle health (button) widget

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_recipe_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_save -> {
                saveRecipe()
                return true
            }

            R.id.action_delete -> {
                showConfirmDeleteRecipeDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmDeleteRecipeDialog() {
        val confirmFragment = ConfirmDeleteRecipeDialog(recipe.name)
        confirmFragment.listener = object : ConfirmDeleteRecipeDialog.ConfirmDeleteDialogListener{
            override fun onDialogPositiveClick() {
                deleteRecipe()
            }

            override fun onDialogNegativeClick() {}

        }

        confirmFragment.show(supportFragmentManager, "confirmDeleteDialog")
    }

    fun deleteRecipe(){
        intent = Intent(ACTION_DELETE_RECIPE)
        intent.putExtra(EXTRA_RECIPE_INDEX, recipe_index)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun saveRecipe(){
        var name_field = findViewById<EditText>(R.id.name)
        var duration_field = findViewById<EditText>(R.id.duration)
        // TODO get ease (rating bar) widget value
        // TODO get score (rating bar) widget value
        var ingredients_field = findViewById<EditText>(R.id.ingredients)
        var steps_field = findViewById<EditText>(R.id.steps)
        var meal_field = findViewById<EditText>(R.id.meal)
        //var price_field = findViewById<EditText>(R.id.price)
        var nutriscore_field = findViewById<EditText>(R.id.nutriscore)
        // TODO get meat (button) widget value
        // TODO get health (button) widget value

        recipe.name = name_field.toString()
        recipe.duration = Integer.valueOf(duration_field.toString())
        // TODO set ease value of recipe object
        // TODO set score value of recipe object
        recipe.ingredient_list = ingredients_field.toString()
        recipe.recipe_steps = steps_field.toString()
        recipe.meal_time = meal_field.toString()
        // TODO set price value of recipe object
        recipe.nutri_score = Integer.valueOf(nutriscore_field.toString())
        // TODO set veggie value of recipe object
        // TODO set healthy value of recipe object

        intent = Intent(ACTION_SAVE_RECIPE)
        intent.putExtra(EXTRA_RECIPE, recipe as Parcelable)
        intent.putExtra(EXTRA_RECIPE_INDEX, recipe_index)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }



}
