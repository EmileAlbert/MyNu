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

class RecipeAdapter(recipes : List<Recipe>, private val itemClickListener: View.OnClickListener, private val itemLongClickListener: View.OnLongClickListener? = null)
    : RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), Filterable {

    private var recipesList = recipes
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

                val tagsFilteredRecipesList = tagsFiltering(tagListFilter)

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

        val tagsFilteredRecipesList = mutableListOf<Recipe>()

        // Tags filtering
       for (recipe in recipesList){

           var filtered = false

           Log.i("FILTER ADAPTER TAG", "$tagsMap")
           Log.i("FILTER ADAPTER RECIPE", "$recipe")

           if (tagsMap["veggie"] != null){

               if (recipe.veggie != tagsMap["veggie"]){filtered = true}
           }

           if (tagsMap["salt"] != null){

               if (recipe.salty != tagsMap["salt"]){filtered = true}
           }

           if (tagsMap["temp"] != null){

               if (recipe.temp != tagsMap["temp"]){filtered = true}
           }

           if (tagsMap["original"] != null){

               if (recipe.original != tagsMap["original"]){filtered = true}
           }

           if (!filtered){

               tagsFilteredRecipesList.add(recipe)
           }

       }

       Log.i("FILTER", "$tagsFilteredRecipesList")

       return tagsFilteredRecipesList

    }

    // return absolute index from filtered index
    fun absoluteIndex(relative_index : Int) : Int{

        var absIndex : Int = -1

        if (recipesList.size == filteredRecipesList.size){

            absIndex = relative_index
        }

        else {

            var index = 0

            for (recipe in recipesList){

                if (recipe == filteredRecipesList[relative_index]){absIndex = index}

                index += 1
            }

        }

        return absIndex
    }

    private fun getPriceSymbol(price : Float) : String {

        var symbol = "€"

        if (price > 5){ symbol += "€"}
        if (price > 8){ symbol += "€"}

        return symbol
    }


}