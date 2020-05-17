package com.example.ebhal.mynu.a_menu

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import com.example.ebhal.mynu.R

class ConfirmModifyDialog : DialogFragment(){

    interface ConfirmModifyDialogListener{
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener : ConfirmModifyDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        super.onCreate(savedInstanceState)

        var day : String? = ""

        // Get arguments
        if (arguments != null) {
            val args = arguments
            day = args?.getString("day")
        }

        val builder = AlertDialog.Builder(activity)

        val msg = getString(R.string.utils_dial_changeMenu_msg)
        val optionModify = getString(R.string.utils_dial_option_modify)
        val optionCancel = getString(R.string.utils_dial_option_cancel)


        builder.setMessage("$msg $day ?")
                .setPositiveButton(optionModify) { _, _ -> listener?.onDialogPositiveClick()}
                .setNegativeButton(optionCancel) { _, _ -> listener?.onDialogNegativeClick()}

        return builder.create()
    }
}
