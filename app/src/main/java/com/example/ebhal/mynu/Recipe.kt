package com.example.ebhal.mynu

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Recipe(var name : String = "default",
                  var duration : Int = 0,
                  var ease : Int = -1,
                  var score : Int = -1,
                  //TODO var ingredient_list : MutableList<String> = arrayListOf(),
                  var ingredient_list : String ="",
                  var recipe_steps : String ="",
                  var price : Float = -1F,
                  var meal_time : String = "",
                  var nutri_score : Int = -1,
                  var veggie : Boolean = false,
                  var healthy : Boolean = false,
                  //TODO var variant : Recipe = null object
                  var filename : String="") : Parcelable, Serializable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
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
        parcel.writeInt(ease)
        parcel.writeInt(score)
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

    companion object CREATOR : Parcelable.Creator<Recipe> {
        private val serialVersionUID: Long = 42424242

        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }


}