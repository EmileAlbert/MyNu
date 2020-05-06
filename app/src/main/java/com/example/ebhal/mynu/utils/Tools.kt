package com.example.ebhal.mynu.utils

import com.example.ebhal.mynu.data.Ingredient

fun add_ing_first2list(ing : Ingredient, ing_list : MutableList<Ingredient>) : MutableList<Ingredient> {

    val res = mutableListOf<Ingredient>(ing)
    res.addAll(ing_list)
    return res
}

