package de.coldtea.moin.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.FragmentAlarmBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlarmFragment: Fragment(){

    // region generated properties
    val viewModel: AlarmViewModel by viewModels()

    var binding: FragmentAlarmBinding? = null
    // endregion

    // region lifecycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        binding?.addAlarm?.setOnClickListener {
            viewModel.testAlarm()
        }

        binding?.getAlarms?.setOnClickListener {
            viewModel.getAlarms()
        }

        return binding?.root
    }

    override fun onResume() {
        startListenAndSetAlarm()
        super.onResume()
    }

    fun startListenAndSetAlarm() = lifecycleScope.launch {
        viewModel.smplrAlarmService.alarmList.collect {
            binding?.text?.text = it.alarmItems.toString()
        }
    }

    // endregion
}