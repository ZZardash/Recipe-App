package com.example.recipe.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import java.lang.reflect.Field

class MinuteTimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TimePicker(context, attrs, defStyleAttr) {

    init {
        setIs24HourView(true)
        setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS)
        disableHourField()
        disableMinuteField()
        disableSecondField()
    }

    private fun disableHourField() {
        try {
            val superclass: Class<*> = javaClass.superclass
            val hourField: Field = superclass.getDeclaredField("mHourSpinner")
            hourField.isAccessible = true
            val hourSpinner: View = hourField.get(this) as View
            hourSpinner.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableMinuteField() {
        try {
            val superclass: Class<*> = javaClass.superclass
            val minuteField: Field = superclass.getDeclaredField("mMinuteSpinner")
            minuteField.isAccessible = true
            val minuteSpinner: View = minuteField.get(this) as View
            minuteSpinner.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableSecondField() {
        try {
            val superclass: Class<*> = javaClass.superclass
            val secondField: Field = superclass.getDeclaredField("mSecondSpinner")
            secondField.isAccessible = true
            val secondSpinner: View = secondField.get(this) as View
            secondSpinner.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}