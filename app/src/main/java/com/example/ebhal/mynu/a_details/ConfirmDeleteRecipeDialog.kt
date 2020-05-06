package com.example.ebhal.mynu.a_details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import com.example.ebhal.mynu.R

class ConfirmDeleteRecipeDialog : DialogFragment(){

    interface ConfirmDeleteDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmDeleteDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        super.onCreate(savedInstanceState)

        Log.i("DialogDeleteConfirm", "$savedInstanceState")
        var recipeName : String? = ""

        // Get arguments
        if (arguments != null) {

            val args = arguments
            recipeName = args?.getString("recipe_name")
        }

        val builder = AlertDialog.Builder(activity)

        val msg = getString(R.string.utils_dial_deleteConfirm_msg)

        val optionSuppress = getString(R.string.utils_dial_option_suppress)
        val optionCancel = getString(R.string.utils_dial_option_cancel)

        val recipeTitle = recipeName

        builder.setMessage("$msg $recipeTitle ?")
                .setPositiveButton(optionSuppress) { _, _ -> listener?.onDialogPositiveClick()}
                .setNegativeButton(optionCancel) { _, _ -> listener?.onDialogNegativeClick()}

        return builder.create()
    }
}
