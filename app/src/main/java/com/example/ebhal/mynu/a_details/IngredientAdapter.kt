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

                        var data = Ingredient()
                        var error = -1

                        if (data.cleanInputName(rcNameView.text.toString()) != ""){

                            val cleanedInput = data.cleanInputName(rcNameView.text.toString())
                            data.name = cleanedInput

                        }

                        else {error += 1}

                        if (rcQtyView.text.toString() != "") {

                            if (error != 0){

                                // Check and clean ingredient quantity input
                                if (data.checkInputQtyIsValid(rcQtyView.text.toString())) {

                                    data.quantity = data.normalizedQuantityForm_fromRaw(rcQtyView.text.toString())

                                    // Check if modification on existing ingredient or add new
                                    if (adapterPosition == 0){
                                        this@IngredientAdapter.addIngredient(data)
                                    }

                                    else {
                                        this@IngredientAdapter.modifyIngredient(data, adapterPosition)
                                    }

                                    rcNameView.post { rcNameView.requestFocus() }
                                }
                            }
                        }

                        else { error += 10}


                        if (error != -1) {

                            Log.w(TAG, "ADD NOT DONE - $error")
                            var errorMSG = context.getString(R.string.recipe_details_error_input_start) + " "

                            when (error) {

                                // Error in ingredient name input
                                0 -> {errorMSG += context.getString(R.string.recipe_details_error_input_name)}

                                // Error in ingredient quantity input
                                9 -> {errorMSG += context.getString(R.string.recipe_details_error_input_quantity)}

                                // Error in quantity user input
                                10 -> {errorMSG += context.getString(R.string.recipe_details_error_input_both)}
                            }

                            Toast.makeText(context, errorMSG, Toast.LENGTH_SHORT).show()
                        }

                        else {Log.i(TAG, "ADD DONE")}

                        true
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
        holder.rcNameView.setText(ingredient.name_toStringUI())
        holder.rcQtyView.setText(ingredient.qty_toStringUI())
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun addIngredient(ingredient : Ingredient) {
        ingredientsList.add(ingredient)
        this.notifyDataSetChanged()
    }

    fun modifyIngredient(ingredient : Ingredient, index : Int) {

        ingredientsList[index] = ingredient
        this.notifyDataSetChanged()
    }

    // Multiply ingredient list
    fun multiplyIngredientList(current : Int, next : Int){


        val use_ingredient_list = mutableListOf<Ingredient>()
        use_ingredient_list.addAll(this.ingredientsList)
        use_ingredient_list.removeAt(0)

        val factor = next.toFloat() / current.toFloat()

        Log.i(TAG, "MULTIPLY INg LIST - $current - $next - $factor : $use_ingredient_list")


        for (ingredient in use_ingredient_list){

            ingredient.quantity = ingredient.OneToNquantity(ingredient.quantity, factor)
        }

        ingredientsList = add_ing_first2list(Ingredient("",""), use_ingredient_list)
        notifyDataSetChanged()
    }

    // each ingredient quantity is returned for 1 guest
    fun get_ingredient_list(N : Int) : MutableList<Ingredient>{

        val raw_list = mutableListOf<Ingredient>()
        raw_list.addAll(this.ingredientsList)

        raw_list.removeAt(0)

        val normList1GuestQty = mutableListOf<Ingredient>()

        for (ingredient in raw_list){

            val raw_qty = ingredient.quantity
            ingredient.quantity = ingredient.Nto1quantity(raw_qty, N)

            normList1GuestQty.add(ingredient)
        }

        return normList1GuestQty
    }

}