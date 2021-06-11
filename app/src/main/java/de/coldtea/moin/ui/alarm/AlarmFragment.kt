package de.coldtea.moin.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.FragmentAlarmBinding
import de.coldtea.moin.services.model.convertToDelegateItem
import de.coldtea.moin.ui.alarm.adapter.AlarmsAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlarmFragment: Fragment(){

    // region properties
    private val viewModel: AlarmViewModel by viewModels()

    private val alarmsAdapter = AlarmsAdapter()

    var binding: FragmentAlarmBinding? = null
    // endregion

    // region lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startListenAndSetAlarm()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        binding?.setUIItems()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAlarms()
    }

    override fun onResume() {
        startListenAndSetAlarm()
        super.onResume()
    }

    fun startListenAndSetAlarm() = lifecycleScope.launch {
        viewModel.smplrAlarmService.alarmList.collect { alarmList ->
            alarmsAdapter.items = alarmList.alarmItems.map { it.convertToDelegateItem() }
            alarmsAdapter.notifyDataSetChanged()
        }
    }

    // endregion

    // region setup

    fun FragmentAlarmBinding.setUIItems(){
        setAlarm.setOnClickListener {
            viewModel.testAlarm()
        }

        alarmList.adapter = alarmsAdapter
    }

    // endregion
}