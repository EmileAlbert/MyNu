package com.example.ebhal.mynu

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Recipe_update(var title : String = "",
                         var time : Int = -1,
                         var ease : Int = -1,
                         var price : Float = -1F,
                         //TODO var ingredient_list : MutableList<String> = arrayListOf(),
                         var recipe_steps : String ="",
                         var score : Int = -1,
                         var nutri_score : Int = -1,
                         var vegie : Boolean = false,
                         var meal_time : String = "",
                         //TODO var variant : Recipe = null object
                         var filename : String="") : Parcelable, Serializable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readFloat(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(time)
        parcel.writeInt(ease)
        parcel.writeFloat(price)
        parcel.writeString(recipe_steps)
        parcel.writeInt(score)
        parcel.writeInt(nutri_score)
        parcel.writeByte(if (vegie) 1 else 0)
        parcel.writeString(meal_time)
        parcel.writeString(filename)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe_update> {
        private val serialVersionUID: Long = 42424242

        override fun createFromParcel(parcel: Parcel): Recipe_update {
            return Recipe_update(parcel)
        }

        override fun newArray(size: Int): Array<Recipe_update?> {
            return arrayOfNulls(size)
        }
    }


}