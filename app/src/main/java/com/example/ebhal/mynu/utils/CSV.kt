package com.example.ebhal.mynu.utils

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.util.ObjectsCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.ebhal.mynu.data.Recipe
import java.io.*

class CSV : AppCompatActivity() {

    private val TAG = "Import_Export"

    fun import (context : Context, csvFile: InputStream?) : MutableList<Recipe> {

        var data = URI2CSV_content(csvFile)
        var list_recipes = importRecipes(context, data)
        return list_recipes

    }


    // Convert an URI to list of CSV content lines
    private fun URI2CSV_content (csvFile : InputStream?) : List<String> {

        val isr = InputStreamReader(csvFile, "UTF-8")
        var list : List<String> = BufferedReader(isr).readLines()
        return list

    }

    // Convert list of content to recipe object
    private fun importRecipes(context: Context, data : List<String>) : MutableList<Recipe> {

        var recipes_import : MutableList<Recipe> = mutableListOf()
        var recipe_data = data.toMutableList()
        val recipe_model = Recipe()
        var list_param_model = ""
        for (param in recipe_model.getListParameters()){list_param_model += param + ","}

        if (!ObjectsCompat.equals(recipe_data[0], list_param_model)){
            Toast.makeText(context, "CSV header incorrect", Toast.LENGTH_LONG).show()
            return recipes_import
        }

        recipe_data.removeAt(0)
        for (recipe_line in recipe_data) run {
            var params: List<String>
            params = recipe_line.split(",")

            var recipe = Recipe(params[0], Integer.valueOf(params[1]), java.lang.Float.valueOf(params[2]),
                    java.lang.Float.valueOf(params[3]), params[4], params[5],
                    java.lang.Float.valueOf(params[6]), params[7], Integer.valueOf(params[8]),
                    params[9].toBoolean(), params[10].toBoolean(), params[11].toBoolean(),
                    params[12].toBoolean(), params[13].toBoolean(), params[14].toBoolean(),java.lang.Long.valueOf(params[15]))

            recipes_import.add(recipe)
        }

        return recipes_import
    }

    fun exportCSV(context: Context, database: Database){
        //Toast.makeText(context, "Export CSV", Toast.LENGTH_SHORT).show()

        var recipes = database.get_recipes()

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
        var fileWriter : FileWriter? = null

        try {

            fileWriter = FileWriter(CSV_File)
            Log.i("ENCODE", fileWriter.encoding)
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
        ContextCompat.startActivity(context, Intent.createChooser(sendIntent, "SHARE"), null)
    }

    companion object
}