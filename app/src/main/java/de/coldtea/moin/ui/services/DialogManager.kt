package de.coldtea.moin.ui.services

import android.app.Activity
import android.app.AlertDialog

object DialogManager {

    @Throws(IllegalStateException::class)
    fun buildDialog(
        activity: Activity?,
        message: String,
        positiveText: String,
        positiveAction: (() -> Unit),
        negativeText: String? = null,
        negativeAction: (() -> Unit)? = null
    ): AlertDialog = activity?.let{
        val builder = AlertDialog.Builder(activity)

        builder.setMessage(message)
        builder.setPositiveButton(positiveText) { _, _ ->
            positiveAction()
        }

        if (negativeText != null && negativeAction != null){
            builder.setNegativeButton(negativeText) { _, _ ->
                negativeAction()
            }
        }

        return builder.create()
    }?: throw IllegalStateException("Activity for the current fragment not found!")

}