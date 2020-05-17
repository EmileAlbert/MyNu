package com.example.ebhal.mynu.a_menu

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Recipe

class ExtraRecipeAdapter(recipes : List<Recipe>,
                         guest_number : List<Int>,
                         empty_recipe_name : String,
                         private val itemClickListener : View.OnClickListener,
                         val saveExtraGuest_database : (Long, Int) -> Boolean,
                         private val readOnly : Boolean)

    : RecyclerView.Adapter<ExtraRecipeAdapter.ViewHolder>() {

    private var extra_recipes_list = recipes as MutableList<Recipe>
    private var extra_recipes_guest_number = guest_number as  MutableList<Int>
    private var empty_recipe_name_inner = empty_recipe_name

    init {

        // Empty extra recipe always at the end with a guest number = 2
        extra_recipes_list.add(Recipe(empty_recipe_name))
        extra_recipes_guest_number.add(2)
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val cardView = itemView.findViewById(R.id.card_view_menu_extra) as CardView
        val recipeTitleView = cardView.findViewById(R.id.extra_rc_recipe_title) as TextView
        val guestView = cardView.findViewById(R.id.extra_rc_person_nb) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_menu_extra, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.i("EXTRA ADAPTER", "$extra_recipes_list")
        holder.cardView.tag = position

        holder.recipeTitleView.text = extra_recipes_list[position].name
        holder.guestView.text = extra_recipes_guest_number[position].toString()


        holder.cardView.setOnClickListener(itemClickListener)

        if (!readOnly || extra_recipes_list[position].name == empty_recipe_name_inner ) {
            holder.guestView.setOnClickListener {
                // Increment guest number
                val current = Integer.valueOf(holder.guestView.text.toString())
                val next = current + 1

                extra_recipes_guest_number[position] = next
                holder.guestView.text = next.toString()
                saveExtraGuest_database(extra_recipes_list[position].id, next)
            }

            holder.guestView.setOnLongClickListener {
                // Decrement guest number
                val current = Integer.valueOf(holder.guestView.text.toString())
                val next = current - 1

                extra_recipes_guest_number[position] = Integer.valueOf(next)
                holder.guestView.text = next.toString()
                saveExtraGuest_database(extra_recipes_list[position].id, next)
                true
            }
        }
    }

    fun getExtraRecipesList_clean(): MutableList<Recipe> {
        val return_list = mutableListOf<Recipe>()
        return_list.addAll(extra_recipes_list)

        return_list.dropLast(1)
        return return_list
    }

    fun getExtraRecipesList(): MutableList<Recipe> {
        val return_list = mutableListOf<Recipe>()
        return_list.addAll(extra_recipes_list)

        return return_list
    }

    fun getExtraRecipesGuests_clean(): MutableList<Int> {
        val return_list = mutableListOf<Int>()
        return_list.addAll(extra_recipes_guest_number)

        return_list.dropLast(1)
        return return_list
    }

    fun getExtraRecipesGuests(): MutableList<Int> {
        val return_list = mutableListOf<Int>()
        return_list.addAll(extra_recipes_guest_number)

        return return_list
    }


    fun getGuestNumberforRecipes() : MutableList<Int>{
        return extra_recipes_guest_number
    }

    override fun getItemCount(): Int {

        return getExtraRecipesList_clean().size
    }

    fun deleteExtraRecipe(index : Int) {

        extra_recipes_list.removeAt(index)
        notifyDataSetChanged()

    }

}