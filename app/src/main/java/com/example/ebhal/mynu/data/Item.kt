package com.example.ebhal.mynu.data

import android.util.Log
import com.example.ebhal.mynu.utils.plurals
import java.math.RoundingMode

data class Item(var name : String = "", var quantity : String = "", var rc_position : Int = -1, var check : Boolean = false, var independence : Boolean = false, var dbID : Long = -1) {

    fun qty_toStringUI(): String {

        val res: String

        if (this.quantity.contains('-')) {

            val strings = this.quantity.split("-")

            val int_res = java.lang.Float.valueOf(strings[0])
            res = int_res.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString()

            if (strings[1] == "i") {
                return res
            } else if (strings[1] == strings[2]) {
                return "$res ${strings[1]}"
            } else {
                return "$res ${strings[1]}${strings[2]}"
            }
        } else res = this.quantity

        return res
    }

    fun name_toStringUI(): String {

        var name = this.name
        Log.w(TAG, "Item name to UI  -  $name")

        if (name == "") {return ""}
        if (this.independence){return name}

        val strings_qty = this.quantity.split("-")
        val quantity = java.lang.Float.valueOf(strings_qty[0])

        if (quantity > 1 && strings_qty[1] == "i"){name = plurals(name)}


        return name
    }
}