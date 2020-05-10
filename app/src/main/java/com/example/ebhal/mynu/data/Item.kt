package com.example.ebhal.mynu.data

import java.math.RoundingMode

data class Item(var name : String = "", var quantity : String = "", var rc_position : Int = -1, var check : Boolean = false, var independence : Boolean = false, var dbID : Long = -1){

    fun qty_toStringUI() : String {

        val res: String

        if (this.quantity.contains('-')){

            val strings = this.quantity.split("-")

            val int_res = java.lang.Float.valueOf(strings[0])
            res = int_res.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString()

            if (strings[1] == "i"){return res}
            else if(strings[1] == strings[2]){return "$res ${strings[1]}"}
            else {return "$res ${strings[1]}${strings[2]}"}
        }

        else res = this.quantity

        return res
    }
}