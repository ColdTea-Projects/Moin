package de.coldtea.moin.ui.alarm.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import de.coldtea.moin.ui.alarm.adapter.delegates.AlarmDelegate
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem
import de.coldtea.moin.ui.diffutils.AlarmsDiffUtilCallback

class AlarmsAdapter: AsyncListDifferDelegationAdapter<AlarmDelegateItem>(AlarmsDiffUtilCallback()){

    init{
        delegatesManager.addDelegate(AlarmDelegate())
    }

}