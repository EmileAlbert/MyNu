package com.example.ebhal.mynu.a_menu

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Recipe

class MenuDayAdapter(val recipes : List<Recipe>,
                     val days : List<String>,
                     val guest_number : List<Int>,
                     val itemClickListener: View.OnClickListener,
                     val itemLongClickListener: View.OnLongClickListener? = null,
                     val saveGuest_database : (Int, Int) -> Boolean,
                     val readOnly : Boolean)

    : RecyclerView.Adapter<MenuDayAdapter.ViewHolder>() {

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

        holder.recipeTitleView.text = recipes[position].name
        holder.guestView.text = days_guest[position].toString()

        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.setOnLongClickListener(itemLongClickListener)

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



}