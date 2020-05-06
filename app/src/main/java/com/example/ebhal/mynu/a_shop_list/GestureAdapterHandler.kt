package com.example.ebhal.mynu.a_shop_list

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class GestureAdapterHandler(private var adapter: ItemAdapter, dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs)
{

    // Ovrride fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
    override fun onMove(recyclerView : RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

        adapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder : RecyclerView.ViewHolder, direction: Int){

        // val callback = GestureAdapterHandler(this, adapter, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
        // Toast.makeText(context, "Swipe : $direction", Toast.LENGTH_SHORT).show()

        if (direction == 4) {
            adapter.delete_item(viewHolder.adapterPosition)
        }
    }
}