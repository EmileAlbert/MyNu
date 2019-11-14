package com.example.ebhal.mynu

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

class ConfirmDeleteRecipeDialog(val recipeTitle: String="") : DialogFragment(){

    interface ConfirmDeleteDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmDeleteDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Supprimer la recette \"$recipeTitle\" ?")
                .setPositiveButton("Supprimer", DialogInterface.OnClickListener{ dialog , id -> listener?.onDialogPositiveClick()})
                .setNegativeButton("Annuler", DialogInterface.OnClickListener{ dialog , id -> listener?.onDialogNegativeClick()})

        return builder.create()
    }
}
