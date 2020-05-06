package com.example.ebhal.mynu.data

import android.util.Log

const val TAG = "Ingredient_class"

data class Ingredient(var name : String = "", var quantity : String = ""){

    // return qty of an ing in a form value-decade-unit to be stored in recipe ing list (string)
    override fun toString(): String {
        return name + "_" + quantity
    }

    // return quantity of the ingredient in a human readable form (ex : 56 mg)
    fun qty_toStringUI() : String {

        var res = ""


        if (this.quantity.contains('-')){

            Log.i(TAG, "QTY to UI : ${this.quantity}")
            val strings = this.quantity.split("-")


            res = strings[0]
            if (strings[1] == "i"){return res}
            else if(strings[1] == strings[2]){return "$res ${strings[1]}"}
            else {return "$res ${strings[1]}${strings[2]}"}
        }

        return res
    }

    // check if an user input is valid to be processed as a quantity
    fun checkInputQtyIsValid(input : String) : Boolean{

        val possible_decade_letters = getDecadePossibilities('a')
        val possible_unit_letters = getUnitPossibilities()

        val raw_input = input.toLowerCase()

        val letters_index = mutableListOf<Int>()
        val letters_list = mutableListOf<Char>()
        var digitCount = 0

        var index = 0
        for (character in raw_input){

            if (character.isLetter()){

                letters_list.add(character)
                letters_index.add(index)

                if (!possible_decade_letters.contains(character) && !possible_unit_letters.contains(character)){
                    Log.i(TAG, "Invalid Qty input : Impossible letter")
                    return false
                }
            }

            if (character.isDigit()){digitCount += 1}

            index += 1
        }

        if (digitCount == 0){
            Log.i(TAG, "Invalid Qty input : no digit")
            return false
        }

        if (letters_index.size > 2){
            Log.i(TAG, "Invalid Qty input : letter count > 2")
            return false
        }

        if (letters_index.size == 2){
            if (!possible_unit_letters.contains(letters_list[1])){
                Log.i(TAG, "Invalid Qty input : letter not ordered")
                return false
            }

            if(letters_index[1]-letters_index[0] != 1) {
                Log.i(TAG, "Invalid Qty input : letter not adjacent")
                return false
            }
        }

        return true
    }

    // return normalized quantity form as value-decade-unit
    fun normalizedQuantityForm_fromRaw(raw_qty: String) : String {

        val raw_qty_string = raw_qty

        val qty_unit = getUnit_fromRaw(raw_qty_string)
        val qty_value = getValue_fromRaw(raw_qty_string, qty_unit)


        return "$qty_value-$qty_unit"
    }

    // return normalized quantity for 1 person
    fun Nto1quantity(norm_qty : String, N : Int) : String{

        val valueDecadeUnitList = norm_qty.split("-")

        val NGuestQty = java.lang.Float.valueOf(valueDecadeUnitList[0])
        val oneGuestQty = NGuestQty / N

        return "$oneGuestQty-${valueDecadeUnitList[1]}-${valueDecadeUnitList[2]}"
    }

    fun OneToNquantity(norm_qty : String, N : Float) : String{

        val valueDecadeUnitList = norm_qty.split("-")

        val GuestQty = java.lang.Float.valueOf(valueDecadeUnitList[0])
        val oneGuestQty = GuestQty * N

        return "$oneGuestQty-${valueDecadeUnitList[1]}-${valueDecadeUnitList[2]}"
    }

    // return float equals to quantity from a raw (not normalized) string
    private fun getValue_fromRaw(raw_qty: String, unit : String): Float {

        val decade_unit_list = unit.split("-")
        val qtyDecade = decade_unit_list[0]
        val qtyUnit = decade_unit_list[1]

        // raw quantity form : valuedecadeunit or value decadeunit
        var quantity_string_raw  = raw_qty
        // Log.i(TAG, "get value : raw qty string = $quantity_string_raw")

        if (qtyUnit != "i") {

            val unit_index = quantity_string_raw.indexOf(qtyUnit)
            val decade_index = quantity_string_raw.indexOf(qtyDecade)

            quantity_string_raw = quantity_string_raw.removeRange(unit_index, unit_index + 1)

            if (unit_index != decade_index) {
                quantity_string_raw = quantity_string_raw.removeRange(decade_index, decade_index + 1)
            }
        }

        Log.i(TAG, "Get Value info : $quantity_string_raw")
        return java.lang.Float.valueOf(quantity_string_raw)
    }

    // return ingredient quantity unit from raw checked string
    fun getUnit_fromRaw(raw_qty: String) : String {

        val unit_letters = getUnitPossibilities()

        val raw_quantity = raw_qty

        var letterCount = 0
        var digitCount = 0

        for (character in raw_quantity){
            if (character.isLetter()){letterCount += 1}
            if (character.isDigit()){digitCount += 1}
        }

        if (letterCount == 0){return "i-i"}

        for (unit_letter in unit_letters){

            if (raw_quantity.toLowerCase().contains(unit_letter)) {

                if (letterCount == 1){return "$unit_letter-$unit_letter"}

                else {
                    val decade_letters = getDecadePossibilities(unit_letter)

                    for (decade_letter in decade_letters){
                        if (raw_quantity.toLowerCase().contains(decade_letter)){
                            return "$decade_letter-$unit_letter"
                        }
                    }
                }
            }
        }

        return "getUnit_fromRaw - Error"
    }


    // return normalized quantity (in g for weight, in l for liquid)
    // Use when this.quantity has a form value-decade-unit
    fun normalizedQuantityValue_fromNorm() : String{

        var norm_qty: String

        val decade = getList_ValueDecadeUnit()[1]
        val unit = getList_ValueDecadeUnit()[2]

        val current_qty_value = java.lang.Float.valueOf(getList_ValueDecadeUnit()[0])

        Log.i(TAG, "NORM QTY Value : ${this.name} - unit = $unit - decade = $decade")

        if (unit == "l"){

            val conversionFactor_map = decade2factor_l()
            val factor = conversionFactor_map[decade]!!

            Log.i(TAG, "factor : $factor")

            norm_qty = (current_qty_value * factor).toString()
            norm_qty += "-l-l"
        }

        else if (unit == "g"){

            val conversionFactor_map = decade2factor_g()
            val factor = conversionFactor_map[decade]!!

            Log.i(TAG, "factor : $factor")

            norm_qty = (current_qty_value * factor).toString()
            norm_qty += "-g-g"
        }

        else {
            norm_qty = current_qty_value.toString() + "-i-i"
        }

        Log.i(TAG, "Normalised quantity : $norm_qty")
        return norm_qty
    }

    fun getList_ValueDecadeUnit(): MutableList<String> {

        return this.quantity.split("-").toMutableList()
    }

    // return the quantity with wished unit decade
    fun norm2decade(value : Float, decade : String, unit : String) : String{

        var converted_value = ""

        if (unit == "l"){

            val conversionFactor_map = decade2factor_l()
            val factor = conversionFactor_map[decade]!!

            Log.i(TAG, "factor : $factor")

            converted_value = (value / factor).toString()
            converted_value += "-$decade-$unit"
        }

        else if (unit == "g"){

            val conversionFactor_map = decade2factor_g()
            val factor = conversionFactor_map[decade]!!

            Log.i(TAG, "factor : $factor")
            Log.i(TAG, "factor : $factor")

            converted_value = (value / factor).toString()
            converted_value += "-$decade-$unit"
        }

        return converted_value

    }

    private fun getUnitPossibilities() : List<Char>{
        return listOf('g', 'l')
    }

    private fun getDecadePossibilities(unit : Char) : List<Char>{

        val gram_decade = listOf<Char>('m', 'k')
        val liter_decade = listOf<Char>('m', 'c', 'd')

        when (unit){
            'g' -> {return gram_decade}
            'l' -> {return liter_decade}
            'a' -> {return gram_decade + liter_decade}
        }

        return listOf<Char>()
    }

    fun decade2factor_l() : MutableMap<String, Float>{

        val decade2factorMap = mutableMapOf<String, Float>()
        val decades = getDecadePossibilities('l')

        //
        for (decade in decades ){

            if (decade == 'm'){decade2factorMap.put(decade.toString(), 0.001f)}
            if (decade == 'c'){decade2factorMap.put(decade.toString(), 0.01f)}
            if (decade == 'd'){decade2factorMap.put(decade.toString(), 0.1f)}
        }

        decade2factorMap.put("l", 1f)
        return decade2factorMap

    }

    fun decade2factor_g() : MutableMap<String, Float>{

        val decade2factorMap = mutableMapOf<String, Float>()
        val decades = getDecadePossibilities('g')

        //
        for (decade in decades ){

            if (decade == 'm'){decade2factorMap.put(decade.toString(), 0.001f)}
            if (decade == 'k'){decade2factorMap.put(decade.toString(), 1000f)}
        }

        decade2factorMap.put("g", 1f)

        return decade2factorMap

    }
}