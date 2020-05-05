package com.example.ebhal.mynu.a_menu

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.example.ebhal.mynu.R

class ConfirmResetDialog : DialogFragment(){

    interface ConfirmResetDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmResetDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        val msg = getString(R.string.utils_dial_resetMenu_msg)

        val optionReset = getString(R.string.utils_dial_option_reset)
        val optionCancel = getString(R.string.utils_dial_option_cancel)


        builder.setMessage(msg)
                .setPositiveButton(optionReset) { _, _ -> listener?.onDialogPositiveClick()}
                .setNegativeButton(optionCancel) { _, _ -> listener?.onDialogNegativeClick()}

        return builder.create()
    }
}
