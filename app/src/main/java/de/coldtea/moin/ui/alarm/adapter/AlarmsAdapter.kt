package de.coldtea.moin.ui.alarm.adapter

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import de.coldtea.moin.ui.alarm.adapter.delegates.AlarmDelegate
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem

class AlarmsAdapter(items: List<AlarmDelegateItem>): ListDelegationAdapter<List<AlarmDelegateItem>>(){

    init{
        delegatesManager.addDelegate(AlarmDelegate())
    }

}