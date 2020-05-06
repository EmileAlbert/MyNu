package com.example.ebhal.mynu.a_main_list

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.example.ebhal.mynu.App
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.a_details.RecipeDetail_Activity
import com.example.ebhal.mynu.a_menu.Menu_activity
import com.example.ebhal.mynu.data.Recipe
import com.example.ebhal.mynu.utils.*


const val TAG = "Recipe List activity"

// parentActivity of the application
class RecipeList_Activity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {

    companion object {

        const val REQUEST_GET_RECIPE = 42

        const val EXTRA_REQUEST_MENU_DAY_INT = "request_menu_day_int"
        const val EXTRA_REQUEST_MENU_DAY = "request_menu_day"
        const val EXTRA_MENU_RECIPE = "day_recipe"
        const val EXTRA_MENU_RECIPE_IDX = "day_recipe_index"

        const val ACTION_GET_DAY_RECIPE = "com.example.ebhal.mynu.actions.ACTION_GET_RECIPE"
    }

    var request_menu_day_int = -1
    var request_menu_day = ""
    var request_day_recipe : Recipe? = null
    var request_day_recipe_idx = -1

    lateinit var adapter: RecipeAdapter
    private lateinit var recipes: MutableList<Recipe>

    lateinit var toolbar : Toolbar

    private lateinit var toolbar_detector: GestureDetectorCompat
    private lateinit var add_button : View

    private lateinit var database : Database

    private val REQUEST_IMPORT = 111

    override fun onCreate(savedInstanceState: Bundle?) {

        database = App.database

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list_)

        // Data management on creation
        recipes = loadRecipes_database(database)

        // Set toolbar and toolbar gesture handling
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar_detector = GestureDetectorCompat(this, GestureListener())
        val toolbar_view = findViewById<View>(R.id.toolbar)
        toolbar_view.setOnTouchListener(object : View.OnTouchListener {

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return toolbar_detector.onTouchEvent(event)
            }
        })

        // Set onTouch listener on Action button (add)
        add_button = findViewById<FloatingActionButton>(R.id.create_recipe_fab)
        add_button.setOnClickListener(this)
    }

    override fun onStart() {

        super.onStart()

        // Request from menu view ?
        request_menu_day_int = intent.getIntExtra(EXTRA_REQUEST_MENU_DAY_INT, -1)
        if (request_menu_day_int >= 0){

            request_menu_day = intent.getStringExtra(EXTRA_REQUEST_MENU_DAY)
            adapter = RecipeAdapter(recipes, this, this)

            toolbar.title = resources.getString(R.string.recipe_list_title_select)

            var message = resources.getString(R.string.recipe_list_menu_request)
            message +=  " $request_menu_day"

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        else {
            adapter = RecipeAdapter(recipes, this)
        }

        // End of recycler view configuration
        val recyclerView = findViewById<RecyclerView>(R.id.recipes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.tagListFilter = mutableMapOf("veggie" to null, "salt" to null, "temp" to null, "original" to null)
    }

    // External events management /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    inner class GestureListener : GestureDetector.SimpleOnGestureListener(){

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VEL_THRESHOLD = 50

        override fun onFling(
                down_event : MotionEvent?,
                move_event : MotionEvent?,
                velocityX: Float,
                velocityY: Float) : Boolean{

            val diffx = move_event?.x?.minus(down_event!!.x) ?: 0.0F
            val diffy = move_event?.y?.minus(down_event!!.y) ?: 0.0F

            return if (Math.abs(diffx) > Math.abs(diffy)){
                // left or right swipe
                if (Math.abs(diffx) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VEL_THRESHOLD){
                    if (diffx > 0){
                        this@RecipeList_Activity.on_toolbar_RightSwipe()
                    }
                    else {
                        this@RecipeList_Activity.on_toolbar_LeftSwipe()
                    }
                    true
                }
                else {
                    super.onFling(down_event, move_event, velocityX, velocityY)
                }
            }

            else{
                // top or bottom swipe not handled here
                super.onFling(down_event, move_event, velocityX, velocityY)
            }
        }
    }

    // Code executed when click on a card of the list (recycler view)
    override fun onClick(view: View) {

        if (view.tag != null) {

            val recipe_index = adapter.absoluteIndex(view.tag as Int)
            showRecipeDetail(recipe_index)

        } else {

            when (view.id) {
                R.id.create_recipe_fab -> showRecipeDetail(-1)
            }
        }
    }

    // OnLongClick listerner - usable only if menu_view caller
    override fun onLongClick(view: View): Boolean {

        if (view.tag != null) {

            returnRecipe2Menu(view.tag as Int)
        }

        return true
    }

    // Right swipe on toolbar event handler
    private fun on_toolbar_RightSwipe() {
        // Toast.makeText(this, "Right swipe", Toast.LENGTH_LONG).show()
        showMenuMaker()
    }

    // Left swipe on toolbar event handler
    private fun on_toolbar_LeftSwipe() {
        // Toast.makeText(this, "Left swipe", Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_import_recipe -> {
                val intent = Intent()
                        .setType("text/csv")
                        .setAction(Intent.ACTION_GET_CONTENT)

                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_IMPORT)

                return true
            }

            R.id.action_export_recipe -> {
                val csv_object = CSV()
                csv_object.exportCSV(this, database)

                return true
            }

            R.id.app_bar_random ->{

                //Toast.makeText(this, "Vogel Pick", Toast.LENGTH_SHORT).show()

                var selected_recipe : Recipe
                var random_index : Int

                random_index = (0..recipes.size).random()
                selected_recipe = recipes[random_index]

                while (selected_recipe.meal_time != "Souper"){
                    random_index = (0..recipes.size).random()
                    selected_recipe = recipes[random_index]
                }

                returnRecipe2Menu(random_index)

                return true
            }

            R.id.app_bar_filter ->{

                Log.i(TAG, "Filter menu item")
                showPopUpFilterWindows()

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Internal events management /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
            REQUEST_IMPORT -> importRecipes(csv_object.import(contentResolver.openInputStream(data.data!!)))
        }
    }

    private fun returnRecipe2Menu(index : Int) {

        val recipe_index = adapter.absoluteIndex(index)

        request_day_recipe = recipes[recipe_index]
        request_day_recipe_idx = recipe_index

        intent = Intent(RecipeList_Activity.ACTION_GET_DAY_RECIPE)
        intent.putExtra(EXTRA_MENU_RECIPE, request_day_recipe as Parcelable)
        intent.putExtra(EXTRA_MENU_RECIPE_IDX, recipe_index)
        intent.putExtra(EXTRA_REQUEST_MENU_DAY_INT, request_menu_day_int)

        setResult(Activity.RESULT_OK, intent)
        finish()

        //Toast.makeText(this, "Recette sélectionnée pour $request_menu_day : $recipe_index", Toast.LENGTH_SHORT).show()
    }

    // Data management ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Add imported recipes to recipes list if does not exist
    private fun importRecipes(imported_recipes: MutableList<Recipe>) {

        if(imported_recipes.size == 0){Toast.makeText(this, "Import échoué", Toast.LENGTH_LONG).show()}

        val imported_recipes_list = imported_recipes
        val existing_recipes_list = database.get_recipes()
        val existing_recipes_name_list = mutableListOf<String>()

        Log.i(TAG, "list of importet recipes : $imported_recipes")

        // Get all existing recipe name
        for (existing_recipe in existing_recipes_list){
            existing_recipes_name_list.add(existing_recipe.name)
        }

        // Import only recipes wich have a name not present in existing_recipes_name_list
        for (imported_recipe in imported_recipes_list) {

            Log.i(TAG, "try to import $imported_recipe")
            if (imported_recipe.name !in existing_recipes_name_list) {

                Log.i(TAG, "import $imported_recipe")
                saveRecipe(imported_recipe, -1)
            }
        }

        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Import réussi", Toast.LENGTH_LONG).show()
    }

    // Create the new recipe object or delete it function of the action contained in the Intent of activity result
    private fun processEditRecipeResult(data: Intent) {
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

    // Save new recipe in recipes list and notify adapter of recycler view
    fun saveRecipe(recipe: Recipe, recipe_index: Int) {

        val result = persistRecipe_database(database, recipe)

        if (recipe_index < 0) {recipes.add(0, recipe)}
        else {recipes[recipe_index] = recipe}

        adapter.notifyDataSetChanged()

        if (result){Toast.makeText(this, resources.getString(R.string.utils_saved), Toast.LENGTH_SHORT).show()}
        else {Toast.makeText(this, resources.getString(R.string.utils_notsaved), Toast.LENGTH_SHORT).show()}
    }

    // Delete recipe from recipes list and notify adapter of recycler view
    private fun deleteRecipe(recipe_index: Int) {

        if (recipe_index < 0) {
            return
        }

        val recipe = recipes.removeAt(recipe_index)
        val result = deleteRecipe_database(database, recipe.id)
        adapter.notifyDataSetChanged()

        if (result){Toast.makeText(this, resources.getString(R.string.utils_deleted), Toast.LENGTH_SHORT).show()}
        else {Toast.makeText(this, resources.getString(R.string.utils_notdeleted), Toast.LENGTH_SHORT).show()}
    }

    // Activity management ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Launch RecipeDetail_Activity activity
    private fun showRecipeDetail(recipe_index: Int) {

        val recipe = if (recipe_index < 0) Recipe() else recipes[recipe_index]

        //Toast.makeText(this, "Recette sélectionnée : ${recipe.title}", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, RecipeDetail_Activity::class.java)

//        if (request_menu_day_int >= 0) {
//            intent.putExtra(RecipeDetail_Activity.REQUEST_CODE, RecipeDetail_Activity.REQUEST_VIEW_RECIPE)
//        }


        intent.putExtra(RecipeDetail_Activity.REQUEST_CODE, RecipeDetail_Activity.REQUEST_EDIT_RECIPE)
        intent.putExtra(RecipeDetail_Activity.EXTRA_RECIPE, recipe as Parcelable)
        intent.putExtra(RecipeDetail_Activity.EXTRA_RECIPE_INDEX, recipe_index)
        intent.putExtra(RecipeDetail_Activity.EXTRA_REQUEST_MENU_DAY_INT, request_menu_day_int)
        intent.putExtra(RecipeDetail_Activity.EXTRA_REQUEST_MENU_DAY, request_menu_day)

        startActivityForResult(intent, RecipeDetail_Activity.REQUEST_EDIT_RECIPE)
    }

    private fun showMenuMaker(){
        val intent = Intent(this, Menu_activity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    @SuppressLint("InflateParams")
    private fun showPopUpFilterWindows() {

        val inflater:LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_filters, null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )


        val root_layout = findViewById<View>(R.id.main_layout)


        popupWindow.showAtLocation(root_layout, Gravity.CENTER, 0,-500)

        val buttonApply = view.findViewById<Button>(R.id.filter_apply_button)
        buttonApply.setOnClickListener {

            doFilter(popupWindow, view)

        }
    }

    private fun doFilter(popup : PopupWindow, view: View) {

        popup.dismiss()

        val veggie_switch = view.findViewById<SeekBar>(R.id.filter_switch_veggie)
        val salt_switch = view.findViewById<SeekBar>(R.id.filter_switch_salty)
        val temp_switch = view.findViewById<SeekBar>(R.id.filter_switch_temp)
        val original_switch = view.findViewById<SeekBar>(R.id.filter_switch_original)

        val veggie = veggie_switch.progress
        val salt = salt_switch.progress
        val temp = temp_switch.progress
        val original = original_switch.progress

        if (veggie == -1){adapter.tagListFilter["veggie"] = true}
        else if (veggie == 0){adapter.tagListFilter["veggie"] = null}
        else {adapter.tagListFilter["veggie"] = false}

        if (salt == -1){adapter.tagListFilter["salt"] = false}
        else if (salt == 0){adapter.tagListFilter["salt"] = null}
        else {adapter.tagListFilter["salt"] = true}

        if (temp == -1){adapter.tagListFilter["temp"] = false}
        else if (temp == 0){adapter.tagListFilter["temp"] = null}
        else {adapter.tagListFilter["temp"] = true}

        if (original == -1){adapter.tagListFilter["original"] = true}
        else if (original == 0){adapter.tagListFilter["original"] = null}
        else {adapter.tagListFilter["original"] = false}

        adapter.filter.filter(null)
    }

    // Toolbar menu management  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Adding menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if (request_menu_day_int >= 0){

            menuInflater.inflate(R.menu.activity_recipes_list_selection, menu)
        }

        else {

            menuInflater.inflate(R.menu.activity_recipes_list, menu)
        }

        val searchViewItem  = menu?.findItem(R.id.app_bar_search)
        val searchInput = searchViewItem?.actionView as SearchView
        searchInput.maxWidth = Integer.MAX_VALUE

        searchInput.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    // Divers  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}