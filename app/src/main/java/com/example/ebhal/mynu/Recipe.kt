package com.example.ebhal.mynu

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Recipe(var name : String = "",
                  var duration : Int = 0,
                  var ease : Float = 0.00f,
                  var score : Float = 0.00f,
                  //TODO var ingredient_list : MutableList<String> = arrayListOf(),
                  var ingredient_list : String ="",
                  var recipe_steps : String ="",
                  var price : Float = 0F,
                  var meal_time : String = "",
                  var nutri_score : Int = 0,
                  var veggie : Boolean = false,
                  var healthy : Boolean = false,
                  //TODO var variant : Recipe = null object
                  var filename : String="") : Parcelable, Serializable {

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
            parcel.readString()) {
    }

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
        parcel.writeByte(if (healthy) 1 else 0)
        parcel.writeString(filename)
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
        list.add("healthy")

        return list
    }

    fun toCSVobject() : String{

        var CSV_txt : String = ""

        CSV_txt += "$name,"
        CSV_txt += "$duration,"
        CSV_txt += "$ease,"
        CSV_txt += "$score,"
        CSV_txt += "$ingredient_list,"
        CSV_txt += "$recipe_steps,"
        CSV_txt += "$price,"
        CSV_txt += "$meal_time,"
        CSV_txt += "$nutri_score,"
        CSV_txt += "$healthy,"
        CSV_txt += "$veggie,"
        CSV_txt += "$name"

        return CSV_txt
    }
    companion object CREATOR : Parcelable.Creator<Recipe> {
        private val serialVersionUID: Long = 4040404040 // TODO Maybe source of error when object definition changes

        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }


}