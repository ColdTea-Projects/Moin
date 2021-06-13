package de.coldtea.moin.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentAlarmBinding
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.moin.services.model.convertToDelegateItem
import de.coldtea.moin.ui.alarm.adapter.AlarmsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.scope.fragmentScope
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class AlarmFragment : Fragment() {

    // region properties
    private val viewModel: AlarmViewModel by viewModels()

    private var alarmsAdapter = AlarmsAdapter(listOf())

    var binding: FragmentAlarmBinding? = null

    // endregion

    // region lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Moin --> onCreate")
        super.onCreate(savedInstanceState)
        startListeningAlarms()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("Moin --> onCreateView")
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        binding?.initUIItems()

        return binding?.root
    }

    override fun onResume() {
        Timber.d("Moin --> onResume")
        super.onResume()

        viewModel.getAlarms()
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
            AppCompatResources.getDrawable(context, R.drawable.divider_alarm_list)?.let { itemDecoration.setDrawable(it) }
            if (itemDecorationCount == 0) addItemDecoration(itemDecoration)

            adapter = alarmsAdapter
        }
    }

    private fun startListeningAlarms() = lifecycleScope.launchWhenCreated {
        viewModel.alarmList.collect { alarmList ->
            alarmsAdapter.items = alarmList.alarmItems.map { it.convertToDelegateItem() }
            alarmsAdapter.notifyDataSetChanged()
        }
    }
    // endregion
}