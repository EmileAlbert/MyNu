package com.example.ebhal.mynu.a_details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.example.ebhal.mynu.R

class BackConfirmDialog : DialogFragment(){

    interface ConfirmBackDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmBackDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        builder.setMessage(getString(R.string.utils_dial_backConfirm_msg))
                .setPositiveButton(getString(R.string.utils_dial_option_continue)) { _, _ -> listener?.onDialogPositiveClick()}
                .setNegativeButton(getString(R.string.utils_dial_option_suppress)) { _, _ -> listener?.onDialogNegativeClick()}

        return builder.create()
    }
}
