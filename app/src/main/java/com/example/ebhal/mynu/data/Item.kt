package com.example.ebhal.mynu.data

data class Item(var name : String = "", var quantity : String = "", var rc_position : Int = -1, var check : Boolean = false, var independence : Boolean = false, var dbID : Long = -1){

    fun qty_toStringUI() : String {

        var res = ""

        if (this.quantity.contains('-')){

            var strings = this.quantity.split("-")

            res = strings[0]
            if (strings[1] == "i"){return res}
            else if(strings[1] == strings[2]){return "$res ${strings[1]}"}
            else {return "$res ${strings[1]}${strings[2]}"}
        }

        else res = this.quantity

        return res
    }
}