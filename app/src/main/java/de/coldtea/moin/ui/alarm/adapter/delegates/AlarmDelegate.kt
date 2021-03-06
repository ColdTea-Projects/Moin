package de.coldtea.moin.ui.alarm.adapter.delegates

import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
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
    ) = holder.bind(item, payloads)

    class AlarmViewHolder(
        private val binding: ViewAlarmDelegateItemBinding,
        private val smplrAlarmService: SmplrAlarmService
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlarmDelegateItem, payloads: MutableList<Any>) = with(binding) {
            val hourMinute = item.originalHour to item.originalMinute
            binding.item = item
            time.text = hourMinute.getTimeText()
            days.text = item.weekDays.getWeekDaysText()

            snooze.text = "Snoozed until ${(item.hour to item.minute).getTimeText()}"
            snooze.isVisible = item.hour != item.originalHour || item.minute != item.originalMinute

            setupCheckList(item.weekDays)

            repeat.isChecked = item.weekDays.isNotEmpty()

            expand.scaleY = if(item.isExpanded) -1F else 1F

            isActive.setOnCheckedChangeListener { _, isChecked ->
                updateAlarm(item, isActive = isChecked, weekDays = weekdaysList)
            }

            expand.setOnClickListener {
                val infoPairs = listOf(
                    "originalHour" to "${item.originalHour}",
                    "originalMinute" to "${item.originalMinute}",
                    "isExpanded" to "${!item.isExpanded}"
                )
                updateAlarm(item, infoPairs = infoPairs, weekDays = weekdaysList)
            }

            delete.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    smplrAlarmService.cancelAlarm(item.requestId)
                }
            }

            repeat.setOnClickListener {
                unifyChecklist(repeat.isChecked)
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
            }

            onWeekdayClicked = {
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
            }

            time.setOnClickListener {
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val infoPairs = listOf(
                        "originalHour" to "${hour}",
                        "originalMinute" to "${minute}",
                        "isExpanded" to "${!item.isExpanded}"
                    )

                    updateAlarm(item = item, hour = hour, minute = minute, infoPairs = infoPairs)
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
            infoPairs: List<Pair<String, String>>? = null
        ) = CoroutineScope(Dispatchers.IO).launch {
            smplrAlarmService.updateAlarm(
                requestId = item.requestId,
                hour = hour?:item.hour,
                minute = minute?:item.minute,
                isActive = isActive?:item.isActive,
                weekDays = weekDays?:item.weekDays,
                infoPairs = infoPairs
            )
        }
    }


}