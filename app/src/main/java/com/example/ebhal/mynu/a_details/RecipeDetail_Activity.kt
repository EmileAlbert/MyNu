package com.example.ebhal.mynu.a_details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.a_main_list.RecipeList_Activity
import com.example.ebhal.mynu.a_menu.Menu_activity
import com.example.ebhal.mynu.data.Ingredient
import com.example.ebhal.mynu.data.Recipe


class RecipeDetail_Activity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val REQUEST_CODE = "request_code"

        val REQUEST_EDIT_RECIPE = 1
        val REQUEST_VIEW_RECIPE = 12
        val EXTRA_RECIPE = "recipe"
        val EXTRA_RECIPE_INDEX = "recipe_index"

        val ACTION_SAVE_RECIPE = "com.example.ebhal.mynu.actions.ACTION_SAVE_RECIPE"
        val ACTION_DELETE_RECIPE = "com.example.ebhal.mynu.actions.ACTION_DELETE_RECIPE"

        val EXTRA_REQUEST_MENU_DAY_INT = "request_menu_day_int"
        val EXTRA_REQUEST_MENU_DAY = "request_menu_day"
    }

    var request_code : Int = -1

    lateinit var recipe : Recipe
    var recipe_index : Int = -1

    var menu_request_day_int = -1
    var menu_request_day = ""

    lateinit var name_View : TextView
    lateinit var duration_View : TextView
    lateinit var price_View : TextView

    lateinit var ease_rating : RatingBar
    lateinit var score_rating : RatingBar

    lateinit var ingredient_list : MutableList<Ingredient>
    lateinit var ingredient_recyclerView : RecyclerView
    lateinit var ingredient_adapter : IngredientAdapter

    lateinit var steps_View : TextView
    lateinit var meal_View : Spinner
    lateinit var nutriscore_View : SeekBar

    lateinit var meat_check : ToggleButton
    lateinit var salt_check : ToggleButton
    lateinit var temp_check : ToggleButton
    lateinit var season_check : ToggleButton
    lateinit var ordinary_check : ToggleButton
    lateinit var original_check : ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        // Set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        toolbar.setNavigationOnClickListener{goBack()}

        request_code = intent.getIntExtra(REQUEST_CODE, -1)

        recipe = intent.getParcelableExtra<Recipe>(EXTRA_RECIPE)

        if (request_code == REQUEST_EDIT_RECIPE){

            recipe_index = intent.getIntExtra(EXTRA_RECIPE_INDEX, -1)

            menu_request_day_int = intent.getIntExtra(EXTRA_REQUEST_MENU_DAY_INT, -1)
            menu_request_day = intent.getStringExtra(EXTRA_REQUEST_MENU_DAY)
        }

        // Get and fill entries
        getEntriesName()
        fillEntrieswithRecipe()


        if (request_code == REQUEST_VIEW_RECIPE){

            toolbar.title = resources.getString(R.string.recipe_details_RO_title)
            makeViewReadOnly()
        }

    }


    // External events management /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onClick(view : View) {
        if (view.tag != null){
            Toast.makeText(this, "Clic sur un ingrédient", Toast.LENGTH_LONG).show()
        }
    }

    // Internal events management /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Get entries name
    fun getEntriesName(){
        name_View = findViewById<TextView>(R.id.name)
        duration_View = findViewById<TextView>(R.id.duration)
        price_View = findViewById<TextView>(R.id.price)

        ease_rating = findViewById<RatingBar>(R.id.ease)
        score_rating = findViewById<RatingBar>(R.id.score)

        ingredient_recyclerView = findViewById<RecyclerView>(R.id.ingredients_recycler_view)

        steps_View = findViewById<TextView>(R.id.steps)
        meal_View = findViewById<Spinner>(R.id.meal)

        nutriscore_View = findViewById<SeekBar>(R.id.nutriscore_seekbar)
        nutriscore_View.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            // TODO Update seekbar UI
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                var nutriscore = recipe.int2nutri(i)
                //Toast.makeText(applicationContext,"Nutriscore : $nutriscore",Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        meat_check = findViewById<ToggleButton>(R.id.meat)
        salt_check = findViewById<ToggleButton>(R.id.salt)
        temp_check = findViewById<ToggleButton>(R.id.temp)
        season_check = findViewById<ToggleButton>(R.id.season)
        ordinary_check = findViewById<ToggleButton>(R.id.ordinary)
        original_check = findViewById<ToggleButton>(R.id.original)
    }

    // Disable entries modification possibility
    private fun makeViewReadOnly() {

        name_View.isEnabled = false
        duration_View.isEnabled = false
        price_View.isEnabled = false

        ease_rating.isEnabled = false
        score_rating.isEnabled = false

        // ing_recycler_view disabled in adapter init  (rc declaration in fillEntrieswithRecipe())

        steps_View.isEnabled = false
        meal_View.isEnabled = false
        nutriscore_View.isEnabled = false
        meat_check.isEnabled = false
        salt_check.isEnabled = false
        temp_check.isEnabled = false
        season_check.isEnabled = false
        ordinary_check.isEnabled = false
        original_check.isEnabled = false
    }

    // Fill entry with existing recipe data received from intent
    fun fillEntrieswithRecipe(){
        // Fill entries with existing value of the selected recipe if existing
        name_View.text = recipe.name
        duration_View.text = recipe.duration.toString()
        price_View.text = recipe.price.toString()

        ease_rating.rating = recipe.ease
        score_rating.rating = recipe.score

        ingredient_list = recipe.ing_string2list(recipe.ingredient_list)
        ingredient_adapter = IngredientAdapter(this, ingredient_list, request_code == REQUEST_VIEW_RECIPE)
        ingredient_recyclerView.layoutManager = LinearLayoutManager(this)
        ingredient_recyclerView.adapter = ingredient_adapter

        steps_View.text = recipe.recipe_steps

        if (meal_View.getItemAtPosition(0) == recipe.meal_time){meal_View.setSelection(0)}
        if (meal_View.getItemAtPosition(1) == recipe.meal_time){meal_View.setSelection(1)}
        if (meal_View.getItemAtPosition(2) == recipe.meal_time){meal_View.setSelection(2)}

        nutriscore_View.progress = recipe.nutri_score

        meat_check.isChecked = recipe.veggie
        salt_check.isChecked = recipe.salty
        temp_check.isChecked = recipe.temp
        season_check.isChecked = recipe.season
        ordinary_check.isChecked = recipe.ordinary
        original_check.isChecked = recipe.original
    }

    // Data management ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create intent to let parent activity delete recipe from database
    fun deleteRecipe(){
        intent = Intent(ACTION_DELETE_RECIPE)
        intent.putExtra(EXTRA_RECIPE_INDEX, recipe_index)

        finishActivity(intent, Activity.RESULT_OK)
    }

    // Save recipe in Recipe object
    fun saveRecipe(){

        if (name_View.text.toString() == ""){
            val message = resources.getString(R.string.recipe_details_save_warning)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            return
        }

        recipe.name = name_View.text.toString()
        recipe.duration = Integer.valueOf(duration_View.text.toString())
        recipe.price  = java.lang.Float.valueOf(price_View.text.toString())

        recipe.ease = ease_rating.rating
        recipe.score = score_rating.rating

        recipe.ingredient_list = recipe.ing_list2string(ingredient_adapter.get_ingredient_list())

        recipe.recipe_steps = steps_View.text.toString()
        recipe.meal_time = meal_View.selectedItem.toString()
        recipe.nutri_score = nutriscore_View.progress

        recipe.veggie = meat_check.isChecked
        recipe.salty = salt_check.isChecked
        recipe.temp = temp_check.isChecked
        recipe.season = season_check.isChecked
        recipe.ordinary = ordinary_check.isChecked
        recipe.original = original_check.isChecked


        intent = Intent(ACTION_SAVE_RECIPE)
        intent.putExtra(EXTRA_RECIPE, recipe as Parcelable)
        intent.putExtra(EXTRA_RECIPE_INDEX, recipe_index)

        finishActivity(intent, Activity.RESULT_OK)
    }

    // Activity management ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun showConfirmDeleteRecipeDialog(recipe : Recipe) {

        val confirmFragment = ConfirmDeleteRecipeDialog()

        // Pass the recipe title to the dialoger
        var args: Bundle? = null
        args?.putString("recipe_name", recipe.name)
        confirmFragment.arguments = args

        confirmFragment.listener = object : ConfirmDeleteRecipeDialog.ConfirmDeleteDialogListener{
            override fun onDialogPositiveClick() {
                deleteRecipe()
            }

            override fun onDialogNegativeClick() {}
        }

        confirmFragment.show(supportFragmentManager, "confirmDeleteDialog")
    }

    private fun goBack() {
        if (request_code == REQUEST_EDIT_RECIPE){
            showBackConfirmDialog()
        }

        else {
            goBacktoMenu()
        }
    }

    private fun goBacktoMenu() {
        val intent = Intent(this, Menu_activity::class.java)
        startActivity(intent)
    }

    fun showBackConfirmDialog() {
        val confirmFragment = BackConfirmDialog()
        confirmFragment.listener = object : BackConfirmDialog.ConfirmBackDialogListener{
            override fun onDialogPositiveClick() {
                //Toast.makeText(this@RecipeDetail_Activity, "Continuer", Toast.LENGTH_LONG).show()
                intent = Intent(ACTION_SAVE_RECIPE)
                finishActivity(intent, Activity.RESULT_CANCELED)
            }

            override fun onDialogNegativeClick() {
                //Toast.makeText(this@RecipeDetail_Activity, "Annuler", Toast.LENGTH_LONG).show()
            }
        }

        confirmFragment.show(supportFragmentManager, "BackConfirmDialog")
    }

    fun finishActivity(intent : Intent, result_code : Int){

        intent.putExtra(RecipeList_Activity.EXTRA_REQUEST_MENU_DAY_INT, menu_request_day_int)
        intent.putExtra(RecipeList_Activity.EXTRA_REQUEST_MENU_DAY, menu_request_day)
        setResult(result_code, intent)
        finish()
    }

    // Toolbar menu management  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if (request_code == REQUEST_EDIT_RECIPE) {

            menuInflater.inflate(R.menu.activity_recipe_detail, menu)
        }

            return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_save -> {
                saveRecipe()
                return true
            }

            R.id.action_delete -> {
                showConfirmDeleteRecipeDialog(recipe)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}

