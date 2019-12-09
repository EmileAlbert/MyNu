package com.example.ebhal.mynu.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.ebhal.mynu.Recipe
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider
import android.support.v4.util.ObjectsCompat.equals
import java.io.*
import com.example.ebhal.mynu.RecipeList_Activity.*

private val TAG = "Import_Export"

fun exportCSV(context: Context, recipes : MutableList<Recipe>){
    //Toast.makeText(context, "Export CSV", Toast.LENGTH_SHORT).show()

    var parameters = mutableListOf<String>()
    if (recipes.size != 0){
        parameters = recipes[0].getListParameters()
    }

    else {return}

    var CSV_HEADER = ""
    for(param in parameters){
        CSV_HEADER += "$param,"
    }

    val CSV_File = File.createTempFile("data", ".csv", context.cacheDir)
    var fileWriter: FileWriter? = null

    try {
        fileWriter = FileWriter(CSV_File)

        fileWriter.append(CSV_HEADER)
        fileWriter.append('\n')

        for (recipe in recipes){
            fileWriter.append(recipe.toCSVobject())
            fileWriter.append("\n")
        }
    }

    catch (e: Exception) {
        e.printStackTrace()
        Log.i(TAG, e.toString())
    }

    fileWriter?.close()

    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val apkURI = FileProvider.getUriForFile(context, context.packageName + ".provider", CSV_File)
    sendIntent.putExtra(Intent.EXTRA_STREAM, apkURI)
    sendIntent.type = "text/csv"
    startActivity(context, Intent.createChooser(sendIntent, "SHARE"), null)
}

fun importCSV(context: Context, data : List<String>) : MutableList<Recipe> {

    var recipes_import : MutableList<Recipe> = mutableListOf()
    var recipe_data = data.toMutableList()
    val recipe_model = Recipe()
    var list_model = ""
    for (param in recipe_model.getListParameters()){list_model += param + ","}

    if (!equals(data[0], list_model)){
        Toast.makeText(context, "CSV header incorrect", Toast.LENGTH_LONG).show()
        return recipes_import
    }

    recipe_data.removeAt(0)
    for (recipe_line in recipe_data) run {
        var params: List<String>
        params = recipe_line.split(",")

        var recipe = Recipe(params[0], Integer.valueOf(params[1]), params[2].toFloat(),
                            params[3].toFloat(), params[4], params[5],
                            params[6].toFloat(), params[7], Integer.valueOf(params[8]),
                            params[9].toBoolean(), params[10].toBoolean())

        recipes_import.add(recipe)
    }

    return recipes_import
}

