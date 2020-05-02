package com.example.ebhal.mynu.a_main_list

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Recipe
import java.util.*

class RecipeAdapter(val recipes : List<Recipe>, val itemClickListener: View.OnClickListener, val itemLongClickListener: View.OnLongClickListener? = null)
    : RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), Filterable {

    var recipes_list = recipes
    var filtered_recipes_list = recipes

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
        val recipe = filtered_recipes_list[position]
        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.setOnLongClickListener(itemLongClickListener)
        holder.cardView.tag = position
        holder.titleView.text = recipe.name
        holder.timeView.text = recipe.duration.toString()
        holder.scoreView.text = recipe.score.toString()

    }

    override fun getItemCount(): Int {
        return filtered_recipes_list.size
    }

    override fun getFilter() : Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {

                    filtered_recipes_list = recipes_list
                }

                // TODO Handle tag search

                // Filter on names
                else {

                    val resultList = mutableListOf<Recipe>()

                    for (recipe in recipes_list) {

                        if (recipe.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(recipe)
                        }
                    }

                    filtered_recipes_list = resultList
                }

                val filterResults = FilterResults()
                filterResults.values = filtered_recipes_list
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filtered_recipes_list = results?.values as MutableList<Recipe>
                notifyDataSetChanged()
            }

        }
    }


}