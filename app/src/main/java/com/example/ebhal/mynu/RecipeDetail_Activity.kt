package com.example.ebhal.mynu

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.RatingBar
import android.widget.TextView
import android.widget.ToggleButton
import java.text.NumberFormat

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

    lateinit var name_View : TextView
    lateinit var duration_View : TextView
    lateinit var ease_rating : RatingBar
    lateinit var score_rating : RatingBar
    lateinit var ingredients_View : TextView
    lateinit var steps_View : TextView
    lateinit var meal_View : TextView
    lateinit var price_View : TextView
    lateinit var nutriscore_View : TextView
    lateinit var health_check : ToggleButton
    lateinit var meat_check : ToggleButton

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
        ease_rating = findViewById(R.id.ease) as RatingBar
        score_rating = findViewById(R.id.score) as RatingBar
        ingredients_View = findViewById(R.id.ingredients) as TextView
        steps_View = findViewById(R.id.steps) as TextView
        price_View = findViewById(R.id.price) as TextView
        meal_View = findViewById(R.id.meal) as TextView
        nutriscore_View = findViewById(R.id.nutriscore) as TextView
        health_check = findViewById(R.id.health) as ToggleButton
        meat_check = findViewById(R.id.meat) as ToggleButton

        // Fill entries with existing value of the selected recipe if existing
        name_View.text = recipe.name
        duration_View.text = recipe.duration.toString()
        ease_rating.rating = recipe.ease
        score_rating.rating = recipe.score
        ingredients_View.text = recipe.ingredient_list
        steps_View.text = recipe.recipe_steps
        price_View.text = recipe.price.toString()
        meal_View.text = recipe.meal_time
        nutriscore_View.text = recipe.nutri_score.toString()
        health_check.isChecked = recipe.healthy
        meat_check.isChecked = recipe.veggie


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
        recipe.name = name_View.text.toString()
        recipe.duration = Integer.valueOf(duration_View.text.toString())
        recipe.ease = ease_rating.getRating()
        recipe.score = score_rating.getRating()
        recipe.ingredient_list = ingredients_View.text.toString()
        recipe.recipe_steps = steps_View.text.toString()
        recipe.meal_time = meal_View.text.toString()
        recipe.price = NumberFormat.getInstance().parse(price_View.getText().toString()).toFloat()
        recipe.nutri_score = Integer.valueOf(nutriscore_View.text.toString())
        recipe.healthy = health_check.isChecked()
        recipe.veggie = meat_check.isChecked()

        intent = Intent(ACTION_SAVE_RECIPE)
        intent.putExtra(EXTRA_RECIPE, recipe as Parcelable)
        intent.putExtra(EXTRA_RECIPE_INDEX, recipe_index)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }



}
