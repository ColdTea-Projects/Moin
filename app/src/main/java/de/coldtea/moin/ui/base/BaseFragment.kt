package de.coldtea.moin.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    var isActionBarVisible: Boolean = false
    set(value) {

        field = value

        if (value) setActionBarVisible()
        else setActionBarInvisible()
    }

    override fun onResume() {
        super.onResume()

        setActionBarVisible()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isActionBarVisible = false
    }

    private fun setActionBarVisible() = (requireActivity() as AppCompatActivity).supportActionBar?.show()
    private fun setActionBarInvisible() = (requireActivity() as AppCompatActivity).supportActionBar?.hide()//TODO: set action bar visibility

}