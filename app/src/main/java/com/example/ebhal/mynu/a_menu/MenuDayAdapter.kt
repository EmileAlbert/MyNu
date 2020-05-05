package com.example.ebhal.mynu.a_menu

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Recipe

class MenuDayAdapter(val recipes : List<Recipe>,
                     val recipes_index : List<Int>,
                     val days : List<String>,
                     val guest_number : List<Int>,
                     val empty_recipe_name : String,
                     val itemClickListener: View.OnClickListener,
                     val randomPick_recipe : (Int) -> Boolean,
                     val saveGuest_database : (Int, Int) -> Boolean,
                     val readOnly : Boolean)

    : RecyclerView.Adapter<MenuDayAdapter.ViewHolder>() {

    var recipes_list = recipes as MutableList<Recipe>
    var recipes_index_list = recipes_index as MutableList<Int>

    private var days_guest : MutableList<Int> = guest_number.toMutableList()

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cardView = itemView.findViewById(R.id.card_view_menu_day) as CardView
        val dayView = cardView.findViewById(R.id.menu_rc_day) as TextView
        val recipeTitleView = cardView.findViewById(R.id.menu_rc_recipe_title) as TextView
        val randomPickView = cardView.findViewById(R.id.menu_random_recipe) as ImageView
        val guestView = cardView.findViewById(R.id.menu_rc_person_nb) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_menu_day, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val day = days[position]
        //val recipe = recipes[position]

        holder.cardView.tag = position
        holder.dayView.text = day

        holder.recipeTitleView.text = recipes_list[position].name
        holder.guestView.text = days_guest[position].toString()

        holder.cardView.setOnClickListener(itemClickListener)

        if (!readOnly) {
            holder.guestView.setOnClickListener {
                // Increment guest number
                val current = Integer.valueOf(holder.guestView.text.toString())
                val next = current + 1

                days_guest[position] = next
                holder.guestView.text = next.toString()
                saveGuest_database(position, next)
            }

            holder.guestView.setOnLongClickListener {
                // Decrement guest number
                val current = Integer.valueOf(holder.guestView.text.toString())
                val next = current - 1

                days_guest[position] = Integer.valueOf(next)
                holder.guestView.text = next.toString()
                saveGuest_database(position, next)
                true
            }

            holder.randomPickView.setOnClickListener{randomPick_recipe(position)}
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

    fun getGuestNumberforDays() : MutableList<Int>{
        return days_guest
    }

    fun swapRecipes(fromPosition : Int, toPosition : Int) : Pair<List<Recipe>, List<Int>> {

        var recipe_swap_1 = recipes_list[fromPosition]
        var recipe_swap_2 = recipes_list[toPosition]

        var recipe_swap_1_index = recipes_index_list[fromPosition]
        var recipe_swap_2_index = recipes_index_list[toPosition]

        if (recipe_swap_1.name != empty_recipe_name){

            recipes_list[fromPosition] = recipe_swap_2
            recipes_list[toPosition] = recipe_swap_1

            recipes_index_list[fromPosition] = recipe_swap_2_index
            recipes_index_list[toPosition] = recipe_swap_1_index

            notifyDataSetChanged()

            return Pair(recipes_list, recipes_index_list)
        }

        Log.i("TAG", "moved item - from $fromPosition")
        Log.i("TAG", "moved item - to $toPosition")

        return Pair(listOf(), listOf())
    }


}