package com.example.ebhal.mynu.a_menu

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

class ConfirmModifyDialog : DialogFragment(){

    interface ConfirmModifyDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmModifyDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Voulez-vous modifier la recette assignée ?")
                .setPositiveButton("Modifier", DialogInterface.OnClickListener{ dialog , id -> listener?.onDialogPositiveClick()})
                .setNegativeButton("Annuler", DialogInterface.OnClickListener{ dialog , id -> listener?.onDialogNegativeClick()})

        return builder.create()
    }
}
