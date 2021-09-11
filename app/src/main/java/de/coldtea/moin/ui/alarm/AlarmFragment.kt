package de.coldtea.moin.ui.alarm

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentAlarmBinding
import de.coldtea.moin.domain.model.alarm.convertToDelegateItem
import de.coldtea.moin.ui.alarm.adapter.AlarmsAdapter
import de.coldtea.moin.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*

class AlarmFragment : BaseFragment() {

    // region properties
    private val viewModel: AlarmViewModel by viewModels()

    private var alarmsAdapter = AlarmsAdapter()

    var binding: FragmentAlarmBinding? = null

    // endregion

    // region lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Moin --> onCreate")
        super.onCreate(savedInstanceState)
        startListeningAlarms()

        isActionBarVisible = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAlarmBinding.inflate(inflater, container, false).apply {
        initAlarmList()
        initFAB()
    }.also{
        Timber.d("Moin --> onCreateView")
        binding = it
    }.root

    override fun onResume() {
        Timber.d("Moin --> onResume")
        super.onResume()

        viewModel.getAlarms()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
    // endregion

    // region init

    private fun FragmentAlarmBinding.initAlarmList() =
        alarmList.apply {
            layoutManager = LinearLayoutManager(context)
            val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
            AppCompatResources.getDrawable(context, R.drawable.divider_alarm_list)?.let { itemDecoration.setDrawable(it) }

            if (itemDecorationCount == 0) addItemDecoration(itemDecoration)

            adapter = alarmsAdapter
        }

    private fun FragmentAlarmBinding.initFAB() =
        setAlarm.setOnClickListener {
//            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                viewModel.setAlarm(hour, minute)
            }

            val alarmTime = getAlarmTime(alarmList.size + 1)
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                alarmTime.first,
                alarmTime.second,
//                cal.get(Calendar.HOUR_OF_DAY),
//                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

    private fun startListeningAlarms() = lifecycleScope.launchWhenCreated {
        viewModel.alarmList.collect { alarmList ->
            alarmsAdapter.items = alarmList.alarmItems.map { it.convertToDelegateItem() }
        }
    }

    private fun getAlarmTime(duration: Int): Pair<Int, Int>
        = Calendar.getInstance().let {
            it.add(Calendar.MINUTE, duration)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }
    // endregion
}