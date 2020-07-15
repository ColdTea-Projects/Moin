package de.coldtea.moin.alarms.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.coldtea.moin.R

class FragmentAlarms : Fragment() {

    private lateinit var viewModelAlarms: ViewModelAlarms

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelAlarms =
            ViewModelProviders.of(this).get(ViewModelAlarms::class.java)
        val root = inflater.inflate(R.layout.moin_fragment_alarms, container, false)
        val textView: TextView = root.findViewById(R.id.tv_alarms)
        viewModelAlarms.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}