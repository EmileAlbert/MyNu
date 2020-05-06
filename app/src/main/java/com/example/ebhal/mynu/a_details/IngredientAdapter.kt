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

class IngredientAdapter(var context : Context, ingredients : MutableList<Ingredient>, val readOnly : Boolean)
    : RecyclerView.Adapter<IngredientAdapter.ViewHolder>(){

    private var ingredientsList = add_ing_first2list(Ingredient("",""), ingredients)

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cardView : CardView
        val rcNameView : EditText
        val rcQtyView : EditText
        private val rcDeleteButton : ImageView

        init {

            cardView = itemView.findViewById(R.id.card_view_ingredient) as CardView

            rcNameView = cardView.findViewById(R.id.rc_ingredient) as EditText
            rcNameView.isFocusableInTouchMode = true
            rcNameView.isFocusable = true

            rcQtyView = cardView.findViewById(R.id.rc_quantity) as EditText
            rcQtyView.setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {

                    EditorInfo.IME_ACTION_DONE -> {
                        if (rcNameView.text.toString() != "" && rcQtyView.text.toString() != "") {

                            val data =  Ingredient(rcNameView.text.toString())

                            if (data.checkInputQtyIsValid(rcQtyView.text.toString())){

                                data.quantity = data.normalizedQuantityForm_fromRaw(rcQtyView.text.toString())

                                this@IngredientAdapter.addIngredient(data)
                                rcNameView.post {rcNameView.requestFocus() }
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
                            if (rcNameView.text.toString() == "") {field = "nom"}

                            Log.w(TAG, "ADD NOT DONE")
                            Toast.makeText(context, "Remplissez le champ $field de l'ingrédient", Toast.LENGTH_SHORT).show()
                            true
                        }
                    }
                    else -> false
                }
            }

            rcDeleteButton = cardView.findViewById(R.id.rc_ingredient_delete) as ImageView
            rcDeleteButton.setOnClickListener{
                Log.w(TAG, "DELETE BUTTON")

                val position = cardView.tag as Int
                ingredientsList.removeAt(position)

                this@IngredientAdapter.notifyDataSetChanged()
            }

            if (readOnly){
                rcNameView.isEnabled = false
                rcQtyView.isEnabled = false
                rcDeleteButton.isEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ingredient = ingredientsList[position]
        holder.cardView.tag = position
        holder.rcNameView.setText(ingredient.name)
        holder.rcQtyView.setText(ingredient.qty_toStringUI())
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun addIngredient(ingredient : Ingredient) {
        ingredientsList.add(ingredient)
        this.notifyDataSetChanged()
    }

    // Multiply ingredient list
    fun multiplyIngredientList(current : Int, next : Int){

        val list = ingredientsList
        val factor = next.toFloat() / current.toFloat()

        Log.i(TAG, "MULTIPLY INg LIST - $current - $next - $factor : $list")


        for (ingredient in ingredientsList){

            ingredient.quantity = ingredient.OneToNquantity(ingredient.quantity, factor)
        }

        ingredientsList = add_ing_first2list(Ingredient("",""), list)
        notifyDataSetChanged()
    }

    // each ingredient quantity is returned for 1 guest
    fun get_ingredient_list(N : Int) : MutableList<Ingredient>{

        val raw_list = mutableListOf<Ingredient>()
        raw_list.addAll(this.ingredientsList)

        Log.i(TAG, "Get ING LIST : $raw_list" + this.ingredientsList)
        raw_list.removeAt(0)
        Log.i(TAG, "Get ING LIST : $raw_list" + this.ingredientsList)

        val normList1GuestQty = mutableListOf<Ingredient>()

        for (ingredient in raw_list){

            val raw_qty = ingredient.quantity
            ingredient.quantity = ingredient.Nto1quantity(raw_qty, N)

            normList1GuestQty.add(ingredient)
        }

        return normList1GuestQty
    }

}