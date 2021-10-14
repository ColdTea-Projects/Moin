package de.coldtea.moin.ui.dialogfragments.label

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import de.coldtea.moin.R
import de.coldtea.moin.databinding.DialogAlarmLabelBinding
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem
import de.coldtea.moin.ui.services.DialogManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmLabelDialogFragment : DialogFragment() {
    private val alarmLabelDialogViewModel: AlarmLabelDialogViewModel by viewModel()
    private var dialogAlarmLabelBinding: DialogAlarmLabelBinding? = null

    var alarmDelegateItem: AlarmDelegateItem? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogAlarmLabelBinding =
            DialogAlarmLabelBinding.inflate(LayoutInflater.from(requireContext()))
        dialogAlarmLabelBinding?.label?.setText(alarmDelegateItem?.label.orEmpty())

        return DialogManager.buildDialog(
            activity = requireActivity(),
            message = "",
            positiveText = getString(R.string.ok),
            positiveAction = ::onPositiveClicked,
            negativeText = getString(R.string.cancel),
            negativeAction = ::onNegativeClicked,
            view = dialogAlarmLabelBinding?.root
        )
    }

    private fun onPositiveClicked(dialogInterface: DialogInterface) = alarmDelegateItem?.let {
        alarmLabelDialogViewModel.saveAlarm(it, dialogAlarmLabelBinding?.label?.text.toString())
        dialogInterface.dismiss()
    }

    private fun onNegativeClicked(dialogInterface: DialogInterface) {
        dialogInterface.dismiss()
    }

    companion object {
        const val TAG = "AlarmLabelDialogFragment"

        fun getInstance(alarmDelegateItem: AlarmDelegateItem) =
            AlarmLabelDialogFragment().apply {
                this.alarmDelegateItem = alarmDelegateItem
            }
    }
}
