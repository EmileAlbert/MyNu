package com.example.ebhal.mynu

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.TextView

class RecipeAdapter(val recipes : List<Recipe>, val itemClickListener: View.OnClickListener)
    : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cardView = itemView.findViewById(R.id.card_view) as CardView
        val titleView = cardView.findViewById(R.id.title) as TextView
        val timeView = cardView.findViewById(R.id.time) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.tag = position
        holder.titleView.text = recipe.title
        holder.timeView.text = recipe.time.toString()
    }

    override fun getItemCount(): Int {
        return recipes.size
    }



}