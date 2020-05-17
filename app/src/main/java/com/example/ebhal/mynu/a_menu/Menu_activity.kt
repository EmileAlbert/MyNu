package com.example.ebhal.mynu.a_menu

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.example.ebhal.mynu.App
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.a_details.RecipeDetail_Activity
import com.example.ebhal.mynu.a_main_list.RecipeList_Activity
import com.example.ebhal.mynu.a_shop_list.Shop_activity
import com.example.ebhal.mynu.a_shop_list.makeShoppingList
import com.example.ebhal.mynu.a_shop_list.shoppingListIsCompleted
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.data.Recipe
import com.example.ebhal.mynu.utils.*

@Suppress("DEPRECATION")
class Menu_activity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "Menu_Activity"

    private lateinit var toolbar_detector: GestureDetectorCompat

    lateinit var menu_adapter: MenuDayAdapter
    lateinit var extra_adapter: ExtraRecipeAdapter

    lateinit var week_days: List<String>
    lateinit var menu_recipes : MutableList<Recipe>
    lateinit var guest_number : MutableList<Int>

    lateinit var extra_recipes : MutableList<Recipe>
    lateinit var extra_guest_number : MutableList<Int>

    private lateinit var database: Database

    private lateinit var shopping_list : MutableList<Item>
    private lateinit var button_shopping_list : Button

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {

        database = App.database

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Toolbar management
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar !!.setDisplayHomeAsUpEnabled(false)

        toolbar_detector = GestureDetectorCompat(this, GestureListener())

        val toolbar_view = findViewById<View>(R.id.toolbar)

        toolbar_view.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return toolbar_detector.onTouchEvent(event)
            }
        })

        // Initialize databse if first time application is opened
        if (database.get_menuDays_count() < 6){resetMenu_database(database)}

        // Load recipes in menu from database
        val res_list_recipes=  loadMenu_database(database, emptyRecipe())
        menu_recipes = res_list_recipes
        guest_number = database.get_daysGuest()

        // Create menu recycler view with read only mode if shopping list started
        val empty_recipe_name = resources.getString(R.string.menu_default_recipe_title)

        week_days = getWeekDaysList()
        menu_adapter = MenuDayAdapter(menu_recipes, week_days, guest_number, empty_recipe_name, this, ::saveGuestNB_database, checkeditem_database(database))

        val menu_recyclerView = findViewById<RecyclerView>(R.id.menu_day_recycler_view)
        menu_recyclerView.layoutManager = LinearLayoutManager(this)
        menu_recyclerView.adapter = menu_adapter

        // Gesture management on menu recycler view
        val callback = GestureAdapterHandler_menu_recyclerView(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(menu_recyclerView)


        // Load extra recipes in menu from database
        extra_recipes = getExtraRecipeList_database(database)
        extra_guest_number = getExtraGuestNumber_database(database)
        val extra_default_recipe_title = getString(R.string.menu_extra_default_recipe_title)

        // Create extra recipes recycler view
        extra_adapter = ExtraRecipeAdapter(extra_recipes, extra_guest_number, extra_default_recipe_title, this, ::saveExtraGuestNB_database, checkeditem_database(database))

        val extra_recyclerView = findViewById<RecyclerView>(R.id.extra_recipe_recycler_view)
        extra_recyclerView.layoutManager = LinearLayoutManager(this)
        extra_recyclerView.adapter = extra_adapter

        // Gesture management on menu recycler view
        val extra_callback = GestureAdapterHandler_extra_recyclerView(ItemTouchHelper.UP, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
        val extra_helper = ItemTouchHelper(extra_callback)
        extra_helper.attachToRecyclerView(extra_recyclerView)

        // Create shopping list button action
        button_shopping_list = findViewById(R.id.menu_button_shopping_list)
        button_shopping_list.setOnClickListener{goToShoppingList()}

        // Disable shopping list button if list is completed
        if (!shoppingListIsEmpty_database(database) && shoppingListIsCompleted(loadShoppingList_database(database))){

            button_shopping_list.isEnabled = false
            button_shopping_list.text = resources.getString(R.string.menu_shopping_list_completed)

            val newDrawable = resources.getDrawable(R.drawable.ic_shopping_full_white_24dp)
            Log.i(TAG, "drawable : $newDrawable")
            button_shopping_list.setCompoundDrawablesWithIntrinsicBounds( null, newDrawable, null, null)
        }



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
                        this@Menu_activity.on_toolbar_RightSwipe()
                    }
                    else {
                        this@Menu_activity.on_toolbar_LeftSwipe()
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

    // Right swipe on toolbar event handler
    private fun on_toolbar_RightSwipe() {
        // Toast.makeText(this, "Right swipe", Toast.LENGTH_LONG).show()
    }

    // Left swipe on toolbar event handler
    private fun on_toolbar_LeftSwipe() {
        // Toast.makeText(this, "Left swipe", Toast.LENGTH_LONG).show()
        // Go Back
        val intent = Intent(this, RecipeList_Activity::class.java)
        intent.putExtra(RecipeList_Activity.EXTRA_REQUEST_MENU_DAY_INT, -1)
        intent.putExtra(RecipeList_Activity.EXTRA_REQUEST_MENU_DAY, "")
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // On cardview click listener - show read only recipe details
    override fun onClick(view : View) {

        Log.i(TAG, "onClick : ${view.id} - ${view.tag}")
        Log.i(TAG, "${R.id.card_view_menu_day}")
        Log.i(TAG, "${R.id.card_view_menu_extra}")

        var selected_recipe : Recipe = Recipe("Error")
        val recipe_index = view.tag as Int

        if (view.tag != null) {

            if (view.id == R.id.card_view_menu_day) {selected_recipe = menu_recipes[recipe_index]}

            if (view.id == R.id.card_view_menu_extra) {selected_recipe = extra_recipes[recipe_index]}

            if (selected_recipe.name != resources.getString(R.string.menu_default_recipe_title) && selected_recipe.name != resources.getString(R.string.menu_extra_default_recipe_title)) {

                showRecipeDetail(selected_recipe)
            }

            else {

                Toast.makeText(this, "Aucune recette choisie", Toast.LENGTH_LONG).show()
            }
        }

    }

    inner class GestureAdapterHandler_menu_recyclerView(dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

        private var dragFrom = -1
        private var dragTo = -1

        override fun onMove(recyclerView : RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition


            if (dragFrom == -1) { dragFrom = fromPosition}
            dragTo = toPosition

//            Auto scroll to allow to swap between extreme cards (useless on One Plus)
//            // Auto scroll of scroll view
//            if (toPosition == 4){
//                scrollview.postDelayed({
//
//                            Log.i(TAG, "Scroll down")
//                            scrollview.smoothScrollBy(0, 250)
//                        },1000
//                )
//            }
//
//            if (toPosition == 3){
//                scrollview.postDelayed({
//
//                            Log.i(TAG, "Scroll up")
//                            scrollview.smoothScrollBy(0, -250)
//                        },1000
//                )
//            }

            Log.i(TAG, "onMove : $toPosition")
            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

            Log.i(TAG, "Clear view - dragFrom = $dragFrom - dragTo = $dragTo")
            super.clearView(recyclerView, viewHolder)

            if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {

                // re ordered list of menu recipes
                val menu_recipes_list = menu_adapter.swapRecipes(dragFrom, dragTo)

                if (menu_recipes_list.isNotEmpty()) {

                    menu_recipes = menu_recipes_list as MutableList<Recipe>

                    AssignDayRecipe(menu_recipes[dragFrom], menu_recipes[dragFrom].id.toInt(), dragFrom)
                    AssignDayRecipe(menu_recipes[dragTo], menu_recipes[dragTo].id.toInt(), dragTo)
                }
            }

            dragFrom = -1
            dragTo = -1
        }

        override fun onSwiped(viewHolder : RecyclerView.ViewHolder, direction: Int) {

            if (direction == 4 && !checkeditem_database(database)) {

                val day = viewHolder.adapterPosition

                // Case when a day has an assigned recipe and is reswiped
                if (menu_recipes[day].id.toInt() != -1){

                    Log.i(TAG, "Swiped ID : ${menu_recipes[day].id.toInt()}")
                    showModifyDialog(day)
                    menu_adapter.notifyItemChanged(day)
                }

                else {
                    goPickRecipe(day)
                }
            }

            else {

                Toast.makeText(this@Menu_activity, getString(R.string.menu_locked), Toast.LENGTH_SHORT).show()
            }

            menu_adapter.notifyDataSetChanged()
        }
    }

    inner class GestureAdapterHandler_extra_recyclerView(dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {return true}

        override fun onSwiped(viewHolder : RecyclerView.ViewHolder, direction: Int) {

            val position = viewHolder.adapterPosition

            // Go pick a recipe
            if (direction == 4 && !checkeditem_database(database)) {

                // Case when a day has an assigned recipe and is reswiped
                if (extra_recipes[position].id.toInt() != -1){

                    Log.i(TAG, "Swiped ID : ${extra_recipes[position].id.toInt()}")
                    showModifyDialog(-1)
                    extra_adapter.notifyItemChanged(position)
                }

                else {
                    goPickRecipe(10, true)
                }
            }

            // Suppress recipe
            else if (direction == 8 && !checkeditem_database(database)) {

                if (extra_recipes[position].name != resources.getString(R.string.menu_extra_default_recipe_title)) {

                    deleteExtraRecipe_database(database, extra_recipes[position])

                    extra_recipes.removeAt(position)
                    extra_guest_number.removeAt(position)

                    Toast.makeText(this@Menu_activity, getString(R.string.extra_menu_recipe_cancelled), Toast.LENGTH_SHORT).show()

                }

                extra_adapter.notifyDataSetChanged()
            }

            else {

                Toast.makeText(this@Menu_activity, getString(R.string.menu_locked), Toast.LENGTH_SHORT).show()
            }

            menu_adapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_reset -> {
                showResetDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Internal events management /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.i(TAG, "onActivityResult")

        // Option 1 : just a RESULT_OK code and there is nothing to do
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            // Option 2 : REQUEST_EDIT_RECIPE and we need to save the new value for the recipe object
            RecipeList_Activity.REQUEST_GET_RECIPE -> getData_recipe(data)
        }
    }

    // Activity management ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Go to recipes list to choose day's recipe
    private fun goPickRecipe(day: Int, extra_recipe : Boolean = false) {

        val day_name : String
        val day_recipe : Recipe
        val day_recipe_idx : Int

        if (extra_recipe){

            day_name = "extra"
            day_recipe = Recipe()
            day_recipe_idx = 0
        }

        else {

            guest_number = menu_adapter.getGuestNumberforDays()

            day_name = getWeekDaysList()[day]
            day_recipe = Recipe()
            day_recipe_idx = 0
        }

        val intent = Intent(this, RecipeList_Activity::class.java)
        intent.putExtra(RecipeList_Activity.EXTRA_REQUEST_MENU_DAY_INT, day)
        intent.putExtra(RecipeList_Activity.EXTRA_REQUEST_MENU_DAY, day_name)
        intent.putExtra(RecipeList_Activity.EXTRA_MENU_RECIPE, day_recipe as Parcelable)
        intent.putExtra(RecipeList_Activity.EXTRA_MENU_RECIPE_IDX, day_recipe_idx)
        startActivityForResult(intent, RecipeList_Activity.REQUEST_GET_RECIPE)
    }

    private fun showResetDialog() {
        val confirmFragment = ConfirmResetDialog()
        confirmFragment.listener = object : ConfirmResetDialog.ConfirmResetDialogListener{
            override fun onDialogPositiveClick() {reset_week_menu()}
            override fun onDialogNegativeClick() {}
        }

        confirmFragment.show(supportFragmentManager, "confirmResetDialog")
    }

    private fun showModifyDialog(day : Int) {

        val confirmFragment = ConfirmModifyDialog()

        val args: Bundle? = null

        if (day > 0){args?.putString("day", Int2Day(day))}
        else {args?.putString("day", "Extra")}

        confirmFragment.arguments = args

        confirmFragment.listener = object : ConfirmModifyDialog.ConfirmModifyDialogListener{
            override fun onDialogPositiveClick() {goPickRecipe(day)}
            override fun onDialogNegativeClick() {}
        }

        confirmFragment.show(supportFragmentManager, "confirmResetDialog")
    }

    private fun goToShoppingList(){

        val recipes_list = menu_recipes
        val days_guest_number = menu_adapter.getGuestNumberforDays()

        val extra_recipes_list = extra_recipes
        val extra_guest_number_list = extra_adapter.getExtraRecipesGuests_clean()

        val recipes_guestNB_list = mutableListOf<Pair<Recipe, Int>>()


        // Construct list of paire recipe - guest number
        var index = 0
        for (recipe in recipes_list){

            val pair = Pair(recipe, days_guest_number[index])
            recipes_guestNB_list.add(pair)

            index += 1
        }

        index = 0
        for (recipe in extra_recipes_list){

            val pair = Pair(recipe, extra_guest_number_list[index])
            recipes_guestNB_list.add(pair)

            index += 1
        }

        Log.i(TAG, "list of pair recipe - guest nb : $recipes_guestNB_list")

        shopping_list = makeShoppingList(recipes_guestNB_list)

        Log.i(TAG, "next -> $shopping_list")

        // shopping list not yet created in database - store it in db
        if (shoppingListIsEmpty_database(database)) {

            Log.i(TAG, "Shopping list empty")
            saveShoppingList_database(database, shopping_list)
        }

        // shopping list already created but not started yet (no item checked)
        else if (!checkeditem_database(database)){

            Log.i(TAG, "Shopping list not empty but not started")

            // replace all item in shopping list except items added independently of any recipe
            val existing_item_database = loadShoppingList_database(database)
            val independent_items = mutableListOf<Item>()

            for (existing_item in existing_item_database){

                if (existing_item.independence){independent_items.add(existing_item)}
            }

            emptyShoppingList_database(database)
            saveShoppingList_database(database, shopping_list + independent_items)
        }

        // shopping list already created and started
        else {

            Log.i(TAG, "Shopping list not empty and started")
        }

        val intent = Intent(this, Shop_activity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
    }

    // Launch RecipeDetail_Activity activity
    private fun showRecipeDetail(recipe : Recipe) {

        val selected_recipe = recipe

        val intent = Intent(this, RecipeDetail_Activity::class.java)
        intent.putExtra(RecipeDetail_Activity.EXTRA_RECIPE, selected_recipe as Parcelable)
        intent.putExtra(RecipeDetail_Activity.REQUEST_CODE, RecipeDetail_Activity.REQUEST_VIEW_RECIPE)

        startActivityForResult(intent, RecipeDetail_Activity.REQUEST_EDIT_RECIPE)
    }

    // Data management ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun getData_recipe(data: Intent) {

        val day = data.getIntExtra(RecipeList_Activity.EXTRA_REQUEST_MENU_DAY_INT, -1)

        Log.i(TAG, "get data recipe : day = $day")
        //Toast.makeText(this, "data : ${recipe.name} - $day", Toast.LENGTH_LONG).show()

//      if (day < 0 || recipe_index < 0){ return }
        if (day < 0){ return }

        when (data.action) {
             RecipeList_Activity.ACTION_GET_DAY_RECIPE-> {

                 val recipe = data.getParcelableExtra<Recipe>(RecipeList_Activity.EXTRA_MENU_RECIPE)

                 // Menu day assignation
                 if (day < 7){AssignDayRecipe(recipe, recipe.id.toInt(), day)}

                 // Extra recipe adding
                 else {AddExtraRecipe(recipe)}

            }
        }
    }

    private fun AssignDayRecipe(recipe : Recipe, recipe_id : Int, day : Int) {

        val days_guest = menu_adapter.getGuestNumberforDays()

        // Save assignation in db
        persistDayMenu_database(database, day, recipe_id)
        persistDayGuest_database(database, day, days_guest[day])

        // Save assignation in activity list
        menu_recipes[day] = recipe
        menu_adapter.notifyDataSetChanged()
    }

    private fun AddExtraRecipe(recipe : Recipe){

        insertExtraRecipe_database(database, recipe)
        setExtraRecipeGuestNB_database(database, recipe.id, 2)

        val size = extra_recipes.size
        extra_recipes.add(size-1, recipe)
        extra_guest_number.add(size-1, 2)
        extra_adapter.notifyDataSetChanged()

        Log.i(TAG, "extra recipes List : $extra_recipes")
    }

    // save guest number in db
    private fun saveGuestNB_database(position : Int, value : Int) : Boolean{

        Log.i(TAG, "Save guest number $value for $position")

        return persistDayGuest_database(database, position, value)
    }

    private fun saveExtraGuestNB_database(recipe_ID : Long, value : Int) : Boolean{

        Log.i(TAG, "Save guest number $value for $recipe_ID extra recipe")

        return setExtraRecipeGuestNB_database(database, recipe_ID, value)
    }

    private fun reset_week_menu() {

        resetMenu_database(database)
        resetExtraRecipe_database(database)
        emptyShoppingList_database(database)

        val intent = Intent(this, Menu_activity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)

        Toast.makeText(this, resources.getString(R.string.menu_reset_msg), Toast.LENGTH_SHORT).show()
    }

    // Toolbar menu management  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Adding menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    // Divers  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun getWeekDaysList(): List<String> {

        val day_1 = resources.getString(R.string.menu_wk_day_6)
        val day_2 = resources.getString(R.string.menu_wk_day_7)
        val day_3 = resources.getString(R.string.menu_wk_day_1)
        val day_4 = resources.getString(R.string.menu_wk_day_2)
        val day_5 = resources.getString(R.string.menu_wk_day_3)
        val day_6 = resources.getString(R.string.menu_wk_day_4)
        val day_7 = resources.getString(R.string.menu_wk_day_5)

        return listOf(day_1, day_2, day_3, day_4, day_5, day_6, day_7)
    }

    fun Int2Day(int : Int) : String{return week_days[int]}

    fun emptyRecipe() : Recipe {
        return Recipe(resources.getString(R.string.menu_default_recipe_title),
                0, 0f, 0f, "","",0f, "",0,
                false, false, false, false, false, false, -1)
    }
}

