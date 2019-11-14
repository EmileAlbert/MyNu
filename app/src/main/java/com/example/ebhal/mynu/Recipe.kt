package com.example.ebhal.mynu

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Recipe(var title : String = "", var time : Int = 0, var price : Float = -1F, var filename : String="") : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readFloat(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(time)
        parcel.writeFloat(price)
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