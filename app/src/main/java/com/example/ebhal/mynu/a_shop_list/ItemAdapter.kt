package com.example.ebhal.mynu.a_shop_list

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Item
import com.example.ebhal.mynu.utils.fake_item_name


const val LOG = "ShoppingList_Adapter"

class ItemAdapter(private var context: Context, shopping_list : MutableList<Item>, val update_lock_button : (List<Item>) -> Boolean): RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    private var items_list = order_clean_list(shopping_list)

    private var items2save_list : MutableList<Item> = shopping_list
    private var items2delete_list = mutableListOf<Item>()

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cardView : CardView
        val rc_sl_nameView : EditText
        val rc_sl_qtyView : EditText
        val rc_sl_checkbox : CheckBox

        init {

            cardView = itemView.findViewById(R.id.card_view_sl_item) as CardView

            rc_sl_nameView = cardView.findViewById(R.id.rc_sl_item_name) as EditText
            rc_sl_nameView.isFocusableInTouchMode = true
            rc_sl_nameView.isFocusable = true

            rc_sl_qtyView = cardView.findViewById(R.id.rc_sl_item_quantity) as EditText
            rc_sl_qtyView.setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {

                    EditorInfo.IME_ACTION_DONE -> {
                        if (rc_sl_nameView.text.toString() != "") {
                            Log.w(LOG, "ADD DONE")

                            val data =  Item(rc_sl_nameView.text.toString(), rc_sl_qtyView.text.toString(), -1,false, true)
                            this@ItemAdapter.add_item(data)
                            rc_sl_nameView.post {rc_sl_nameView.requestFocus() }
                            true
                        }

                        else {
                            Log.w(LOG, "ADD NOT DONE")
                            true
                        }
                    }
                    else -> false
                }
            }

            rc_sl_checkbox = cardView.findViewById(R.id.rc_sl_item_checked) as CheckBox
            rc_sl_checkbox.setOnCheckedChangeListener { _, isChecked ->

                val position = cardView.tag as Int
                items_list[position].check = isChecked

                update_lock_button(items_list)

                //Toast.makeText(this@ItemAdapter.context,"${isChecked.toString()} + : ${position.toString()} ",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.item_shop_list, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items_list[position]
        item.rc_position = position

        holder.cardView.tag = position
        holder.rc_sl_nameView.setText(item.name)
        holder.rc_sl_qtyView.setText(item.qty_toStringUI())
        holder.rc_sl_checkbox.isChecked = item.check

        Log.i(TAG, "onBindViewHolder ${item.name} position : $position")
    }

    private fun order_clean_list(unordered_list : MutableList<Item>): MutableList<Item> {


        Log.i(TAG, "ORDER - unordered $unordered_list")

        val ordered_list = mutableListOf<Item>()

        for (item in unordered_list) {

            if (item.name != fake_item_name) {
                ordered_list.add(item)
            }
        }

        // re order list function of rc_position item parameter
        ordered_list.sortBy { it.rc_position }

        Log.i(TAG, "ORDER - ordered $ordered_list")

        return add_item_first2list(Item("", "", 0), ordered_list)

    }

    fun swapItems(fromPosition : Int, toPosition : Int){

        // make first item (user input) un movable
        if (fromPosition == 0 || toPosition == 0){return}

        if (fromPosition < toPosition) {

            for (i in fromPosition..toPosition - 1) {

                items_list.set(i, items_list.set(i+1, items_list.get(i)))
            }
        }

        else {

            for (i in fromPosition..toPosition + 1) {

                items_list.set(i, items_list.set(i-1, items_list.get(i)))
            }
        }

        notifyItemMoved(fromPosition, toPosition)
        Log.i("TAG", "moved item - old list : $items_list")
        update_itemRCPos_listIndex()
        Log.i("TAG", "moved item - new list : $items_list")
    }

    private fun update_itemRCPos_listIndex() {

        var index = 0
        for (item in items_list){

            item.rc_position = index
            index += 1
        }
    }

    override fun getItemCount(): Int {
        return items_list.size
    }

    fun add_item(item : Item) {

        items_list.add(item)
        items2save_list.add(item)
        this.notifyDataSetChanged()
    }

    fun delete_item(item_index : Int) {

        val item = items_list[item_index]
        items_list.remove(item)
        this.notifyDataSetChanged()

        items2delete_list.add(item)

        Toast.makeText(this@ItemAdapter.context,"Item supprimé",Toast.LENGTH_SHORT).show()
    }

    fun get_items_list() : MutableList<Item>{

        return items2save_list
    }

    fun get_items2delete_list() : MutableList<Item> {

        return items2delete_list
    }
}