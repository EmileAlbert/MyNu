package com.example.ebhal.mynu.a_shop_list

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.example.ebhal.mynu.App
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.a_menu.Menu_activity
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.utils.*

class Shop_activity : AppCompatActivity() {

    private val TAG = "Shop_Activity"

    private var shoppingIsStarted : Boolean = false
    private var activityMenu : Menu? = null

    private lateinit var adapter: ItemAdapter
    private lateinit var items: MutableList<Item>

    var toolbar : Toolbar? = null

    private lateinit var database : Database

    private lateinit var buttonMenuBack : Button

    override fun onCreate(savedInstanceState: Bundle?) {

        database = App.database

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_list)

        // Set toolbar and toolbar gesture handling
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar?.navigationIcon = null

        // Load shopping list from database
        items = loadShoppingList_database(database)
        Log.i(TAG, "Load shopping list : $items")

        adapter = ItemAdapter(this, items, ::updateLockButton)

        val recyclerView = findViewById<RecyclerView>(R.id.shop_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val callback = GestureAdapterHandler(adapter, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)

        shoppingIsStarted = checkeditem_database(database)

        buttonMenuBack = findViewById(R.id.shop_list_button)
        buttonMenuBack.setOnClickListener { goMenuBack() }

        //Toast.makeText(this, "fixed : ${checkeditem_database(database)}", Toast.LENGTH_SHORT).show()
    }

    private fun goMenuBack() {

        Log.i(TAG, "Saved list : ${adapter.get_items_list()}")

        // Update existing item and create new ones
        val shoppingListUpdated = adapter.get_items_list()
        updateShoppingList_database(database, shoppingListUpdated)

        // Suppress item in 2delete list
        val itemsToDelete = adapter.get_items2delete_list()
        for (item in itemsToDelete){

            database.delete_item(item.dbID)
        }

        val intent = Intent(this, Menu_activity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.activity_shopping_list_menu, menu)
        activityMenu = menu

        val menuItem = menu?.getItem(0)

        if (shoppingIsStarted){

            menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_white_24dp)
        }

        else {

            menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_open_white_24dp)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.shopping_list_lock -> {

                shoppingIsStarted = !shoppingIsStarted

                if (shoppingIsStarted){

                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_white_24dp)
                }

                else {

                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_open_white_24dp)
                }

                fixedShoppingList_database(database, shoppingIsStarted)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateLockButton(items_list : List<Item>) : Boolean {

        var lock = false
        val menuItem = activityMenu!!.findItem(R.id.shopping_list_lock)

        for (item in items_list){if (item.check){lock = true}}

        if (lock){

            shoppingIsStarted = lock

            menuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_white_24dp)
            menuItem.isEnabled = false
        }

        else {

            shoppingIsStarted = lock
            menuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_open_white_24dp)
            menuItem.isEnabled = true
        }

        return true
    }


}
