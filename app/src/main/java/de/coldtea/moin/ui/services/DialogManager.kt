package de.coldtea.moin.ui.services

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View

object DialogManager {

    @Throws(IllegalStateException::class)
    fun buildDialog(
        activity: Activity?,
        message: String,
        title: String? = null,
        positiveText: String,
        positiveAction: ((dialogInterface: DialogInterface) -> Unit),
        negativeText: String? = null,
        negativeAction: ((dialogInterface: DialogInterface) -> Unit)? = null,
        view: View? = null
    ): AlertDialog = activity?.let{
        val builder = AlertDialog.Builder(activity)

        builder.setMessage(message)
        builder.setPositiveButton(positiveText) { dialog, _ ->
            positiveAction(dialog)
        }

        if(view != null){
            builder.setView(view)
        }

        if (title != null){
            builder.setTitle(title)
        }

        if (negativeText != null && negativeAction != null){
            builder.setNegativeButton(negativeText) { dialog, _ ->
                negativeAction(dialog)
            }
        }

        return builder.create()
    }?: throw IllegalStateException("Activity for the current fragment not found!")

}