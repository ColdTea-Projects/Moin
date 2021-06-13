package de.coldtea.moin.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentAlarmBinding
import de.coldtea.moin.services.model.convertToDelegateItem
import de.coldtea.moin.ui.alarm.adapter.AlarmsAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlarmFragment : Fragment() {

    // region properties
    private val viewModel: AlarmViewModel by viewModels()

    private var alarmsAdapter = AlarmsAdapter(listOf())

    var binding: FragmentAlarmBinding? = null
    // endregion

    // region lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startListeningAlarms()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        binding?.initUIItems()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        viewModel.getAlarms()
    }

    fun startListeningAlarms() = lifecycleScope.launch {
        viewModel.smplrAlarmService.alarmList.collect { alarmList ->
            alarmsAdapter.items = alarmList.alarmItems.map { it.convertToDelegateItem() }
            alarmsAdapter.notifyDataSetChanged()
        }
    }

    // endregion

    // region init

    fun FragmentAlarmBinding.initUIItems() {
        setAlarm.setOnClickListener {
            viewModel.testAlarm()
        }

        alarmList.apply {
            layoutManager = LinearLayoutManager(context)
            val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
            context.getDrawable(R.drawable.divider_alarm_list)?.let { itemDecoration.setDrawable(it) }
            if (itemDecorationCount == 0) addItemDecoration(itemDecoration)

            adapter = alarmsAdapter
        }
    }

    // endregion
}