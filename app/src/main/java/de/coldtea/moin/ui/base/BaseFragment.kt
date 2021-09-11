package de.coldtea.moin.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    var isActionBarVisible: Boolean = false
    set(value) {

        field = value

        if (value) setActionBarVisible()
        else setActionBarInvisible()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isActionBarVisible = false
    }

    private fun setActionBarVisible() = requireActivity().actionBar?.show()
    private fun setActionBarInvisible() = requireActivity().actionBar?.hide()//TODO: set action bar visibility

}