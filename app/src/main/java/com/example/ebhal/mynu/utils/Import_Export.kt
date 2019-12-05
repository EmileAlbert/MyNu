package com.example.ebhal.mynu.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.ebhal.mynu.Recipe
import java.io.File
import java.io.FileWriter
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider






private val TAG = "Import_Export"

//class GenericFileProvider : FileProvider()

fun exportCSV(context: Context, recipes : MutableList<Recipe>){
    //Toast.makeText(context, "Export CSV", Toast.LENGTH_SHORT).show()

    val CSV_HEADER = "id,name,address,age"


    val CSV_File = File.createTempFile("data", ".csv", context.cacheDir)
    var fileWriter: FileWriter? = null

    Toast.makeText(context, "1_${CSV_File.name}", Toast.LENGTH_SHORT).show()

    try {
        fileWriter = FileWriter(CSV_File)

        fileWriter.append(CSV_HEADER)
        fileWriter.append('\n')

        fileWriter.append("aaaaa")
        fileWriter.append(',')
        fileWriter.append("bbbbb")
        fileWriter.append(',')
        fileWriter.append("cccccc")
        fileWriter.append(',')
        fileWriter.append("dddddd")
        fileWriter.append('\n')

    } catch (e: Exception) {
        e.printStackTrace()
        Log.i(TAG, e.toString())
        //Toast.makeText(context, "error${e.toString()}", Toast.LENGTH_SHORT).show()
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
