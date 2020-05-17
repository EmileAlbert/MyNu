package com.example.ebhal.mynu.utils

import com.example.ebhal.mynu.data.Ingredient

fun add_ing_first2list(ing : Ingredient, ing_list : MutableList<Ingredient>) : MutableList<Ingredient> {

    val res = mutableListOf<Ingredient>(ing)
    res.addAll(ing_list)
    return res
}

fun plurals(input : String) : String {

    var name  = input
    if (name.split(" ").size == 1) {

        // Exception
        if (name.toLowerCase() == "chou") {name += "x"}
        else if (name.toLowerCase() == "ail") {}

        else {name += "s"}
    }

    // grammar rules for compound name
    else if (name.split(" ").size == 2) {


    }

    else if (name.split(" ").size == 3) {


    }

    return name
}
