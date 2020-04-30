package com.example.ebhal.mynu.data

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.io.Serializable

data class Recipe(var name : String = "",
                  var duration : Int = 0,
                  var ease : Float = 0.00f,
                  var score : Float = 0.00f,
                  var ingredient_list : String = "",
                  var recipe_steps : String ="",
                  var price : Float = 0.00f,
                  var meal_time : String = "",
                  var nutri_score : Int = 0,    // 1 : A - 2 : B - 3 : C - 4 : D - 5 : E
                  var veggie : Boolean = false,
                  var salty : Boolean = false,
                  var temp : Boolean = false,
                  var season : Boolean = false,
                  var ordinary : Boolean = false,
                  var original : Boolean = false,
                  var id : Long = -1) : Parcelable, Serializable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readString(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(duration)
        parcel.writeFloat(ease)
        parcel.writeFloat(score)
        parcel.writeString(ingredient_list)
        parcel.writeString(recipe_steps)
        parcel.writeFloat(price)
        parcel.writeString(meal_time)
        parcel.writeInt(nutri_score)
        parcel.writeByte(if (veggie) 1 else 0)
        parcel.writeByte(if (salty) 1 else 0)
        parcel.writeByte(if (temp) 1 else 0)
        parcel.writeByte(if (season) 1 else 0)
        parcel.writeByte(if (ordinary) 1 else 0)
        parcel.writeByte(if (original) 1 else 0)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getListParameters() : MutableList<String> {
        var list = mutableListOf<String>()

        list.add("name")
        list.add("duration")
        list.add("ease")
        list.add("score")
        list.add("ingredient_list")
        list.add("recipe_steps")
        list.add("price")
        list.add("meal_time")
        list.add("nutri_score")
        list.add("veggie")
        list.add("salty")
        list.add("temp")
        list.add("season")
        list.add("ordinary")
        list.add("original")
        list.add("id")

        return list
    }

    fun toCSVobject() : String{

        var CSV_txt : String = ""

        CSV_txt += "$name" + ","
        CSV_txt += "$duration" + ","
        CSV_txt += "$ease" + ","
        CSV_txt += "$score" + ","
        CSV_txt += "$ingredient_list" + ","
        CSV_txt += "$recipe_steps" + ","
        CSV_txt += "$price" + ","
        CSV_txt += "$meal_time" + ","
        CSV_txt += "$nutri_score" + ","
        CSV_txt += "$veggie" + ","
        CSV_txt += "$salty" + ","
        CSV_txt += "$temp" + ","
        CSV_txt += "$season" + ","
        CSV_txt += "$ordinary" + ","
        CSV_txt += "$original" + ","
        CSV_txt += "$id"

        return CSV_txt
    }

    fun ing_list2string(ing_list : MutableList<Ingredient>) : String{

        var list_str = ""
        var i = 0

        for (ingredient in ing_list){
            if (i == 0){
                list_str += ingredient.toString()
            }

            else {
                list_str += ";" + ingredient.toString()
            }

            i++
        }

        return list_str
    }

    fun ing_string2list(ing_list_str : String) : MutableList<Ingredient>{

        var ingredients_list = mutableListOf<Ingredient>()
        var ingredient_qty_list = ing_list_str.split(";")

        if(ing_list_str != "") {
            for (ing_qty in ingredient_qty_list) {
                Log.w("Recipe", "STR2LST $ing_qty")
                var ing_qty_couple = ing_qty.split("_")
                ingredients_list.add(Ingredient(ing_qty_couple[0], ing_qty_couple[1]))
            }
        }

        return ingredients_list
    }

    fun int2nutri(int : Int) : String{
        val int2nutriscore = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D", 4 to "E")
        return int2nutriscore[int]!!
    }

    fun nutri2int(nutriscore : String) : Int{
        val nutriscore2int = mapOf("A" to 0, "B" to 1, "C" to 2, "D" to 3, "E" to 4)
        return nutriscore2int[nutriscore]!!
    }


    companion object CREATOR : Parcelable.Creator<Recipe> {
        private val serialVersionUID: Long = 18042020 // TODO Maybe source of error when object definition changes

        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }


}