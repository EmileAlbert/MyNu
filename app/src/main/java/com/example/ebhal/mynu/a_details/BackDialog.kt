package com.example.ebhal.mynu.a_details

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

class BackConfirmDialog : DialogFragment(){

    interface ConfirmBackDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmBackDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        builder.setMessage("En continuant vous perdrez les modifications")
                .setPositiveButton("Continuer", DialogInterface.OnClickListener{ dialog, id -> listener?.onDialogPositiveClick()})
                .setNegativeButton("Annuler", DialogInterface.OnClickListener{ dialog, id -> listener?.onDialogNegativeClick()})

        return builder.create()
    }
}
