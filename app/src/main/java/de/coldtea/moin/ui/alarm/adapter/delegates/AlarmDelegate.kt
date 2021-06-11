package de.coldtea.moin.ui.alarm.adapter.delegates

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ViewAlarmDelegateItemBinding
import de.coldtea.moin.extensions.getTimeText
import de.coldtea.moin.extensions.getWeekDaysText
import de.coldtea.moin.services.model.AlarmItem
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem

class AlarmDelegate:
    AbsListItemAdapterDelegate<AlarmDelegateItem, AlarmDelegateItem, AlarmDelegate.AlarmViewHolder>() {

    override fun isForViewType(
        item: AlarmDelegateItem,
        items: MutableList<AlarmDelegateItem>,
        position: Int
    ): Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup): AlarmViewHolder =
        AlarmViewHolder(ViewAlarmDelegateItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))

    override fun onBindViewHolder(
        item: AlarmDelegateItem,
        holder: AlarmViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    class AlarmViewHolder(val binding: ViewAlarmDelegateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlarmDelegateItem){
            val time = item.hour to item.minute
            binding.time.text = time.getTimeText()
            binding.days.text = item.weekDays.getWeekDaysText()
            binding.isActive.isChecked = item.isActive
        }
    }
}