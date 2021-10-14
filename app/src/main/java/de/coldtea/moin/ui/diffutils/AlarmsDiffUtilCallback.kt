package de.coldtea.moin.ui.diffutils

import androidx.recyclerview.widget.DiffUtil
import de.coldtea.moin.ui.alarm.adapter.model.AlarmBundle

class AlarmsDiffUtilCallback : DiffUtil.ItemCallback<AlarmBundle>() {

    override fun areItemsTheSame(oldItem: AlarmBundle, newItem: AlarmBundle): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: AlarmBundle, newItem: AlarmBundle): Boolean =
        oldItem.hashCode() == newItem.hashCode()

}