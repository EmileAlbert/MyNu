package com.example.ebhal.mynu.a_main_list

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.ebhal.mynu.R
import com.example.ebhal.mynu.data.Recipe
import java.util.*

class RecipeAdapter(val recipes : List<Recipe>, val itemClickListener: View.OnClickListener, val itemLongClickListener: View.OnLongClickListener? = null)
    : RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), Filterable {

    var recipesList = recipes
    var filteredRecipesList = recipes

    var tagListFilter : MutableMap<String, Boolean?> = mutableMapOf("veggie" to null, "salt" to null, "temp" to null, "original" to null)

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cardView = itemView.findViewById(R.id.card_view_recipe) as CardView
        val titleView = cardView.findViewById(R.id.recipe_rc_name) as TextView
        val timeView = cardView.findViewById(R.id.recipe_rc_duration) as TextView
        val scoreView = cardView.findViewById(R.id.recipe_rc_score) as TextView
        val priceView = cardView.findViewById(R.id.recipe_rc_price) as TextView

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
        val recipe = filteredRecipesList[position]
        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.setOnLongClickListener(itemLongClickListener)

        holder.cardView.tag = position

        holder.titleView.text = recipe.name
        holder.timeView.text = recipe.duration.toString()
        holder.scoreView.text = recipe.score.toString()
        holder.priceView.text = getPriceSymbol(recipe.price)
    }

    override fun getItemCount(): Int {
        return filteredRecipesList.size
    }

    override fun getFilter() : Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charConstraint = !constraint.isNullOrEmpty()
                val charSearch = constraint.toString()

                var tagsFilteredRecipesList = tagsFiltering(tagListFilter)

                // Name filtering
                if (!charConstraint) {

                    filteredRecipesList = tagsFilteredRecipesList
                }

                else {

                    val resultList = mutableListOf<Recipe>()

                    for (recipe in tagsFilteredRecipesList) {

                        if (recipe.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(recipe)
                        }
                    }

                    filteredRecipesList = resultList
                }

                val filterResults = FilterResults()

                Log.i("FILTER", "Filter results $filteredRecipesList")
                filterResults.values = filteredRecipesList

                return filterResults
            }

            @Suppress("UNCHECKED_CAST")

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredRecipesList = results?.values as MutableList<Recipe>
                notifyDataSetChanged()
            }

        }
    }


    private fun tagsFiltering(tagsMap : MutableMap<String, Boolean?>) : MutableList<Recipe> {

        var tagsFilteredRecipesList = mutableListOf<Recipe>()

        var veggieList = mutableListOf<Recipe>()
        var saltyList = mutableListOf<Recipe>()
        var tempList = mutableListOf<Recipe>()
        var originalList = mutableListOf<Recipe>()

        val tags = tagsMap

        // Tags filtering
       for (recipe in recipesList){

           var filtered = false

           Log.i("FILTER ADAPTER TAG", "$tagListFilter")
           Log.i("FILTER ADAPTER RECIPE", "$recipe")

           if (tagListFilter["veggie"] != null){

               if (recipe.veggie != tagListFilter["veggie"]){filtered = true}
           }

           if (tagListFilter["salt"] != null){

               if (recipe.salty != tagListFilter["salt"]){filtered = true}
           }

           if (tagListFilter["temp"] != null){

               if (recipe.temp != tagListFilter["temp"]){filtered = true}
           }

           if (tagListFilter["original"] != null){

               if (recipe.original != tagListFilter["original"]){filtered = true}
           }

           if (!filtered){

               tagsFilteredRecipesList.add(recipe)
           }

       }

       Log.i("FILTER", "$tagsFilteredRecipesList")

       return tagsFilteredRecipesList

    }

    // return absolute index from filtered index
    fun aboslute_index(relative_index : Int) : Int{

        var abs_index : Int = -1

        if (recipesList.size == filteredRecipesList.size){

            abs_index = relative_index
        }

        else {

            var index = 0

            for (recipe in recipesList){

                if (recipe.equals(filteredRecipesList[relative_index])){abs_index = index}

                index += 1
            }

        }

        return abs_index
    }

    fun getPriceSymbol(price : Float) : String {

        var symbol = "€"

        if (price > 5){ symbol += "€"}
        if (price > 8){ symbol += "€"}

        return symbol
    }


}