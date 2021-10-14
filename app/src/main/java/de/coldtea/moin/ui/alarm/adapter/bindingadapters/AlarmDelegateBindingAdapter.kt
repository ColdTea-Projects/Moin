package de.coldtea.moin.ui.alarm.adapter.bindingadapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible

import androidx.databinding.BindingAdapter
import de.coldtea.moin.R


@BindingAdapter("app:checkedState")
fun setCheckedState(switchCompat: SwitchCompat, isChecked: Boolean) {
    switchCompat.isChecked = isChecked
    switchCompat.jumpDrawablesToCurrentState()
}

@BindingAdapter("app:isVisible")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("app:setLabel")
fun setLabel(view: AppCompatTextView, text: String) {
    view.text = text
    if(text.isEmpty()){
        view.hint = view.context.getString(R.string.label_hint)
    }
}