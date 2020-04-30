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
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Item


const val LOG = "ShoppingList_Adapter"

class ItemAdapter(context : Context, var shopping_list : MutableList<Item>): RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    private var items_list : MutableList<Item> = add_item_first2list(Item("", ""), shopping_list)
    private var context = context

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cardView : CardView
        val rc_sl_nameView : EditText
        val rc_sl_qtyView : EditText
        //val rc_sl_delete_button : ImageView
        val rc_sl_checkbox : CheckBox

        init {
            cardView = itemView.findViewById(R.id.card_view_sl_item) as CardView

            rc_sl_nameView = cardView.findViewById(R.id.rc_sl_item_name) as EditText
            rc_sl_nameView.isFocusableInTouchMode = true
            rc_sl_nameView.isFocusable = true

            rc_sl_qtyView = cardView.findViewById(R.id.rc_sl_item_quantity) as EditText
            rc_sl_qtyView.setOnEditorActionListener { v, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {

                    EditorInfo.IME_ACTION_DONE -> {
                        if (rc_sl_nameView.text.toString() != "") {
                            Log.w(LOG, "ADD DONE")

                            var data =  Item(rc_sl_nameView.text.toString(), rc_sl_qtyView.text.toString(), false, true)
                            this@ItemAdapter.add_item(data)
                            rc_sl_nameView.post(Runnable {rc_sl_nameView.requestFocus() })
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

//            rc_sl_delete_button = cardView.findViewById(R.id.rc_sl_item_delete) as ImageView
//            rc_sl_delete_button.setOnClickListener{
//                Log.w(TAG, "DELETE BUTTON")
//
//                var position = cardView.tag as Int
//                items_list.removeAt(position)
//
//                this@ItemAdapter.notifyDataSetChanged()
//            }

            rc_sl_checkbox = cardView.findViewById(R.id.rc_sl_item_checked) as CheckBox
            rc_sl_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

                var position = cardView.tag as Int
                items_list[position].check = isChecked

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
        holder.cardView.tag = position
        holder.rc_sl_nameView.setText(item.name)
        holder.rc_sl_qtyView.setText(item.qty_toStringUI())
        holder.rc_sl_checkbox.isChecked = item.check

    }

    override fun getItemCount(): Int {
        return items_list.size
    }

    fun add_item(item : Item) {
        items_list.add(item)
        this.notifyDataSetChanged()
    }

    fun get_items_list() : MutableList<Item>{
        var res = items_list
        res.removeAt(0)
        return res
    }
}