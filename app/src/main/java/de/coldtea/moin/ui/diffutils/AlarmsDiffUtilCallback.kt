package de.coldtea.moin.ui.diffutils

import androidx.recyclerview.widget.DiffUtil
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem

class AlarmsDiffUtilCallback : DiffUtil.ItemCallback<AlarmDelegateItem>() {

    override fun areItemsTheSame(oldItem: AlarmDelegateItem, newItem: AlarmDelegateItem): Boolean =
        oldItem.requestId == newItem.requestId

    override fun areContentsTheSame(oldItem: AlarmDelegateItem, newItem: AlarmDelegateItem): Boolean =
        oldItem.requestId == newItem.requestId

}