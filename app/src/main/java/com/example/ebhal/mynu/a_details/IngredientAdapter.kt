package com.example.ebhal.mynu.a_details

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Ingredient
import com.example.ebhal.mynu.utils.add_ing_first2list


private const val TAG = "Ingredient_Adapter"

class IngredientAdapter(var context : Context, val ingredients : MutableList<Ingredient>, val readOnly : Boolean)
    : RecyclerView.Adapter<IngredientAdapter.ViewHolder>(){

    private var ingredients_list = add_ing_first2list(Ingredient("",""), ingredients)

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cardView : CardView
        val rc_nameView : EditText
        val rc_qtyView : EditText
        val rc_delete_button : ImageView

        init {

            cardView = itemView.findViewById(R.id.card_view_ingredient) as CardView

            rc_nameView = cardView.findViewById(R.id.rc_ingredient) as EditText
            rc_nameView.isFocusableInTouchMode = true
            rc_nameView.isFocusable = true

            rc_qtyView = cardView.findViewById(R.id.rc_quantity) as EditText
            rc_qtyView.setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {

                    EditorInfo.IME_ACTION_DONE -> {
                        if (rc_nameView.text.toString() != "" && rc_qtyView.text.toString() != "") {

                            var data =  Ingredient(rc_nameView.text.toString())

                            if (data.checkInputQtyIsValid(rc_qtyView.text.toString())){

                                data.quantity = data.normalizedQuantityForm_fromRaw(rc_qtyView.text.toString())

                                this@IngredientAdapter.add_ingredient(data)
                                rc_nameView.post(Runnable {rc_nameView.requestFocus() })
                                Log.i(TAG, "ADD DONE")
                                true
                            }

                            else {
                                Log.w(TAG, "ADD NOT DONE")
                                Toast.makeText(context, "Rentrez une quantité valide pour l'ingrédient", Toast.LENGTH_SHORT).show()
                                true
                            }
                        }

                        else {

                            var field = "quantité"
                            if (rc_nameView.text.toString() == "") {field = "nom"}

                            Log.w(TAG, "ADD NOT DONE")
                            Toast.makeText(context, "Remplissez le champ $field de l'ingrédient", Toast.LENGTH_SHORT).show()
                            true
                        }
                    }
                    else -> false
                }
            }

            rc_delete_button = cardView.findViewById(R.id.rc_ingredient_delete) as ImageView
            rc_delete_button.setOnClickListener{
                Log.w(TAG, "DELETE BUTTON")

                val position = cardView.tag as Int
                ingredients_list.removeAt(position)

                this@IngredientAdapter.notifyDataSetChanged()
            }

            if (readOnly){
                rc_nameView.isEnabled = false
                rc_qtyView.isEnabled = false
                rc_delete_button.isEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ingredient = ingredients_list[position]
        holder.cardView.tag = position
        holder.rc_nameView.setText(ingredient.name)
        holder.rc_qtyView.setText(ingredient.qty_toStringUI())
    }

    override fun getItemCount(): Int {
        return ingredients_list.size
    }

    fun add_ingredient(ingredient : Ingredient) {
        ingredients_list.add(ingredient)
        this.notifyDataSetChanged()
    }

    // Multiply ingredient list
    fun multIngredientList(current : Int, next : Int){

        val list = ingredients_list
        val factor = next.toFloat() / current.toFloat()

        Log.i(TAG, "MULT INg LIST - $current - $next - $factor : $list")


        for (ingredient in ingredients_list){

            ingredient.quantity = ingredient.OneToNquantity(ingredient.quantity, factor)
        }

        ingredients_list = add_ing_first2list(Ingredient("",""), list)
        notifyDataSetChanged()
    }

    // each ingredient quantity is returned for 1 guest
    fun get_ingredient_list(N : Int) : MutableList<Ingredient>{

        val raw_list = ingredients_list

        Log.i(TAG, "Get ING LIST : $raw_list")
        raw_list.removeAt(0)
        Log.i(TAG, "Get ING LIST : $raw_list")

        var normList1GuestQty = mutableListOf<Ingredient>()

        for (ingredient in raw_list){

            var raw_qty = ingredient.quantity
            ingredient.quantity = ingredient.Nto1quantity(raw_qty, N)

            normList1GuestQty.add(ingredient)
        }

        return normList1GuestQty
    }

}