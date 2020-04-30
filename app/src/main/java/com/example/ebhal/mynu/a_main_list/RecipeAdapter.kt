package com.example.ebhal.mynu.a_main_list

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Recipe

class RecipeAdapter(val recipes : List<Recipe>, val itemClickListener: View.OnClickListener, val itemLongClickListener: View.OnLongClickListener? = null)
    : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    var recipes_list = recipes

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cardView = itemView.findViewById(R.id.card_view_recipe) as CardView
        val titleView = cardView.findViewById(R.id.recipe_rc_name) as TextView
        val timeView = cardView.findViewById(R.id.recipe_rc_duration) as TextView
        val scoreView = cardView.findViewById(R.id.recipe_rc_score) as TextView

//        init {
//            itemView.setOnClickListener{
//                onItemClick?.invoke(recipes_list[adapterPosition])
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes_list[position]
        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.setOnLongClickListener(itemLongClickListener)
        holder.cardView.tag = position
        holder.titleView.text = recipe.name
        holder.timeView.text = recipe.duration.toString()
        holder.scoreView.text = recipe.score.toString()

    }

    override fun getItemCount(): Int {
        return recipes_list.size
    }



}