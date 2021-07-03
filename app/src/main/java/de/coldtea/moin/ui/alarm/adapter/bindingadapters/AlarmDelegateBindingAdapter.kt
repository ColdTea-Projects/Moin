package de.coldtea.moin.ui.alarm.adapter.bindingadapters

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible

import androidx.databinding.BindingAdapter


@BindingAdapter("app:checkedState")
fun setCheckedState(switchCompat: SwitchCompat, isChecked: Boolean) {
    switchCompat.isChecked = isChecked
    switchCompat.jumpDrawablesToCurrentState()
}
@BindingAdapter("app:isVisible")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}