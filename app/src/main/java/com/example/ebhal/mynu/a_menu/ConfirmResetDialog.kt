package com.example.ebhal.mynu.a_menu

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

class ConfirmResetDialog : DialogFragment(){

    interface ConfirmResetDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmResetDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Voulez-vous réinitialiser le menu de la semaine ?")
                .setPositiveButton("Réinitialiser", DialogInterface.OnClickListener{ dialog , id -> listener?.onDialogPositiveClick()})
                .setNegativeButton("Annuler", DialogInterface.OnClickListener{ dialog , id -> listener?.onDialogNegativeClick()})

        return builder.create()
    }
}
