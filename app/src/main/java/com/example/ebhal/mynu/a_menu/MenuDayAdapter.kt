package com.example.ebhal.mynu.a_menu

import android.graphics.Typeface
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Recipe
import java.util.*

class MenuDayAdapter(recipes : List<Recipe>,
                     recipes_index : List<Int>,
                     private val days : List<String>,
                     guest_number : List<Int>,
                     private val empty_recipe_name : String,
                     private val itemClickListener: View.OnClickListener,
                     val saveGuest_database : (Int, Int) -> Boolean,
                     private val readOnly : Boolean)

    : RecyclerView.Adapter<MenuDayAdapter.ViewHolder>() {

    private var recipes_list = recipes as MutableList<Recipe>
    private var recipes_index_list = recipes_index as MutableList<Int>

    private var days_guest : MutableList<Int> = guest_number.toMutableList()

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cardView = itemView.findViewById(R.id.card_view_menu_day) as CardView
        val dayView = cardView.findViewById(R.id.menu_rc_day) as TextView
        val recipeTitleView = cardView.findViewById(R.id.menu_rc_recipe_title) as TextView
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
        if (position == weekDay()) {
            Log.i("MENU DAY ADAPTER", "$day - ${Calendar.DAY_OF_WEEK}")
            holder.dayView.setTypeface(null, Typeface.BOLD_ITALIC)
        }

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
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

    fun getGuestNumberforDays() : MutableList<Int>{
        return days_guest
    }

    fun swapRecipes(fromPosition : Int, toPosition : Int) : Pair<List<Recipe>, List<Int>> {

        val recipe_swap_1 = recipes_list[fromPosition]
        val recipe_swap_2 = recipes_list[toPosition]

        val recipe_swap_1_index = recipes_index_list[fromPosition]
        val recipe_swap_2_index = recipes_index_list[toPosition]

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

    fun weekDay() : Int {

        val calendar = Calendar.getInstance()

        Log.i("MENU DAY ADAPTER", "${calendar.time} - ${Calendar.DAY_OF_WEEK}")
        return calendar.get(Calendar.DAY_OF_WEEK)

    }
}