package de.coldtea.moin.ui.alarm.adapter

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import de.coldtea.moin.ui.alarm.adapter.delegates.AlarmDelegate
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem

class AlarmsAdapter: ListDelegationAdapter<List<AlarmDelegateItem>>(){

    fun alarmAdapter(items: List<AlarmDelegateItem>){
        delegatesManager.addDelegate(AlarmDelegate())
    }

}