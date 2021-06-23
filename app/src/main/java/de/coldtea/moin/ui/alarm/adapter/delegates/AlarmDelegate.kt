package de.coldtea.moin.ui.alarm.adapter.delegates

import android.app.TimePickerDialog
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
import java.util.*

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

                updateAlarm(item, isActive = isChecked, weekDays = weekdaysList)
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
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
            }

            onWeekdayClicked = {
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
            }

            time.setOnClickListener {
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    updateAlarm(item, hour, minute)
                }
                TimePickerDialog(
                    time.context,
                    timeSetListener,
                    item.hour,
                    item.minute,
                    true
                ).show()
            }

        }

        private fun updateAlarm(
            item: AlarmDelegateItem,
            hour: Int? = null,
            minute: Int? = null,
            isActive: Boolean? = null,
            weekDays: List<WeekDays>? = null,
        ) = CoroutineScope(Dispatchers.IO).launch {
            smplrAlarmService.updateAlarm(
                requestId = item.requestId,
                hour = hour?:item.hour,
                minute = minute?:item.minute,
                isActive = isActive?:item.isActive,
                weekDays = weekDays?:item.weekDays
            )
        }
    }


}