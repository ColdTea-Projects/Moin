package de.coldtea.moin.ui.alarm.adapter.delegates

import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.coldtea.moin.databinding.ViewAlarmDelegateItemBinding
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.extensions.*
import de.coldtea.moin.ui.alarm.adapter.model.AlarmBundle
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class AlarmDelegate :
    AbsListItemAdapterDelegate<AlarmBundle, AlarmBundle, AlarmDelegate.AlarmViewHolder>() {

    private val smplrAlarmService: SmplrAlarmService by KoinJavaComponent.inject(SmplrAlarmService::class.java)

    override fun isForViewType(
        item: AlarmBundle,
        items: MutableList<AlarmBundle>,
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
        item: AlarmBundle,
        holder: AlarmViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item, payloads)

    class AlarmViewHolder(
        private val binding: ViewAlarmDelegateItemBinding,
        private val smplrAlarmService: SmplrAlarmService
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bundle: AlarmBundle, payloads: MutableList<Any>) = with(binding) {

            initActionListeners(bundle)

            if(payloads.isNotEmpty()) upgradeByPayload(payloads[0])
            else drawAlarmListItem(bundle.alarmDelegateItem)

        }

        private fun ViewAlarmDelegateItemBinding.initActionListeners(bundle: AlarmBundle){
            val item = bundle.alarmDelegateItem

            isActive.setOnCheckedChangeListener { _, isChecked ->
                item.hour = item.originalHour
                item.minute = item.originalMinute

                updateAlarm(item, isActive = isChecked, weekDays = weekdaysList)
                cleanSnoozeView()
            }

            expand.setOnClickListener {
                val infoPairs = listOf(
                    "originalHour" to "${item.originalHour}",
                    "originalMinute" to "${item.originalMinute}",
                    "isExpanded" to "${!item.isExpanded}",
                    "label" to item.label
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

            sunday.setOnClickListener {
                item.hour = item.originalHour
                item.minute = item.originalMinute

                updateAlarm(item, isActive = true, weekDays = weekdaysList)
                cleanSnoozeView()
            }
            monday.setOnClickListener {
                item.hour = item.originalHour
                item.minute = item.originalMinute
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
                cleanSnoozeView()
            }
            tuesday.setOnClickListener {
                item.hour = item.originalHour
                item.minute = item.originalMinute
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
                cleanSnoozeView()
            }
            wednesday.setOnClickListener {
                item.hour = item.originalHour
                item.minute = item.originalMinute
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
                cleanSnoozeView()
            }
            thursday.setOnClickListener {
                item.hour = item.originalHour
                item.minute = item.originalMinute
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
                cleanSnoozeView()
            }
            friday.setOnClickListener {
                item.hour = item.originalHour
                item.minute = item.originalMinute
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
                cleanSnoozeView()
            }
            saturday.setOnClickListener {
                item.hour = item.originalHour
                item.minute = item.originalMinute
                updateAlarm(item, isActive = true, weekDays = weekdaysList)
                cleanSnoozeView()
            }

            label.setOnClickListener {
                bundle.onClickLabel(bundle.alarmDelegateItem)
            }

            time.setOnClickListener {
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val infoPairs = listOf(
                        "originalHour" to "$hour",
                        "originalMinute" to "$minute",
                        "isExpanded" to "${!item.isExpanded}",
                        "label" to item.label
                    )

                    updateAlarm(item = item, hour = hour, minute = minute, isActive = true, infoPairs = infoPairs)
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
