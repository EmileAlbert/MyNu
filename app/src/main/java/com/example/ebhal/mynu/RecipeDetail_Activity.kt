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

    lateinit var  titleView : TextView
    lateinit var  textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar !!.setDisplayHomeAsUpEnabled(true)

        recipe = intent.getParcelableExtra<Recipe>(EXTRA_RECIPE)
        recipe_index = intent.getIntExtra(EXTRA_RECIPE_INDEX, -1)

        titleView = findViewById(R.id.title) as TextView
        textView = findViewById(R.id.time) as TextView
        textView = findViewById(R.id.price) as TextView

        titleView.text = recipe.title
        textView.text = recipe.time.toString()
        titleView.text = recipe.price.toString()
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
        val confirmFragment = ConfirmDeleteRecipeDialog(recipe.title)
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
        var title_field = findViewById<EditText>(R.id.title)
        var time_field = findViewById<EditText>(R.id.time)
        var price_field = findViewById<EditText>(R.id.price)

        recipe.title = title_field.toString()
        recipe.time = Integer.valueOf(textView.text.toString())


        intent = Intent(ACTION_SAVE_RECIPE)
        intent.putExtra(EXTRA_RECIPE, recipe as Parcelable)
        intent.putExtra(EXTRA_RECIPE_INDEX, recipe_index)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }



}
