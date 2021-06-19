package de.coldtea.moin.ui.alarm.adapter.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ViewAlarmDelegateItemBinding
import de.coldtea.moin.extensions.*
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class AlarmDelegate :
    AbsListItemAdapterDelegate<AlarmDelegateItem, AlarmDelegateItem, AlarmDelegate.AlarmViewHolder>() {

    private val smplrAlarmService: SmplrAlarmService by KoinJavaComponent.inject(SmplrAlarmService::class.java)

    override fun isForViewType(
        item: AlarmDelegateItem,
        items: MutableList<AlarmDelegateItem>,
        position: Int
    ): Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup): AlarmViewHolder =
        AlarmViewHolder(
            ViewAlarmDelegateItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), smplrAlarmService
        )

    override fun onBindViewHolder(
        item: AlarmDelegateItem,
        holder: AlarmViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    class AlarmViewHolder(
        private val binding: ViewAlarmDelegateItemBinding,
        private val smplrAlarmService: SmplrAlarmService
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlarmDelegateItem) = with(binding) {
            val hourMinute = item.hour to item.minute
            time.text = hourMinute.getTimeText()
            days.text = item.weekDays.getWeekDaysText()
            isActive.isChecked = item.isActive

            setupCheckList(item.weekDays)

            repeat.isChecked = item.weekDays.isNotEmpty()

            isActive.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) activateAlarmUI()
                else deactivateAlarmUI()

                updateAlarm(item, isChecked, weekdaysList)
            }

            expand.setOnClickListener {
                groupHidden.isVisible = !groupHidden.isVisible
                groupWeekdays.isVisible = repeat.isChecked && groupHidden.isVisible

                expand.scaleY = expand.scaleY * -1

                days.isVisible = !groupHidden.isVisible
                days.text = weekdaysList.getWeekDaysText()
            }

            delete.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    smplrAlarmService.cancelAlarm(item.requestId)
                }
            }

            repeat.setOnClickListener {
                groupWeekdays.isVisible = repeat.isChecked && groupHidden.isVisible
                unifyChecklist(repeat.isChecked)
                updateAlarm(item, true, weekdaysList)
            }

            onWeekdayClicked = {
                updateAlarm(item, true, weekdaysList)
            }

        }

        private fun updateAlarm(item: AlarmDelegateItem, isActive: Boolean, weekDays: List<WeekDays>) = CoroutineScope(Dispatchers.IO).launch {
            smplrAlarmService.updateAlarm(
                requestId = item.requestId,
                hour = item.hour,
                minute = item.minute,
                isActive = isActive,
                weekDays = weekDays
            )
        }
    }


}