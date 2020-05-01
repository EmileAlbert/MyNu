package com.example.ebhal.mynu.a_shop_list

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Button
import com.example.ebhal.mynu.App
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.a_menu.Menu_activity
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.utils.Database
import com.example.ebhal.mynu.utils.loadShoppingList_database
import com.example.ebhal.mynu.utils.updateShoppingList_database

class Shop_activity : AppCompatActivity() {

    lateinit var adapter: ItemAdapter
    lateinit var items: MutableList<Item>

    lateinit var toolbar : Toolbar

    private lateinit var database : Database

    private lateinit var button_menu_back : Button

    override fun onCreate(savedInstanceState: Bundle?) {

        database = App.database

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_list)

        // Set toolbar and toolbar gesture handling
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = null

        // Load shopping list from database
        items = loadShoppingList_database(database)

        adapter = ItemAdapter(this, items)

        val recyclerView = findViewById<RecyclerView>(R.id.shop_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val callback = GestureAdapterHandler(this, adapter, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)


        button_menu_back = findViewById(R.id.shop_list_button)
        button_menu_back.setOnClickListener { goMenuBack() }
    }

    private fun goMenuBack() {

        // Update existing item and create new ones
        var shopping_list_updated = adapter.get_items_list()
        updateShoppingList_database(database, shopping_list_updated)

        // Suppress item in 2delete list
        var items_toDelete = adapter.get_items2delete_list()
        for (item in items_toDelete){

            database.delete_item(item.dbID)
        }

        val intent = Intent(this, Menu_activity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
    }
}