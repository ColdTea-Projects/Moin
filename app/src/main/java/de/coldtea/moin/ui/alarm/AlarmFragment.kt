package de.coldtea.moin.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentAlarmBinding

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
    ): View = FragmentAlarmBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    // endregion
}