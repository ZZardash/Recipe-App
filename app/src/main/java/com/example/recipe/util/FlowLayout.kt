package com.example.recipe.util

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var x = paddingLeft
        var y = paddingTop
        val width = r - l

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (x + childWidth + paddingRight > width) {
                // Move to the next line if there's not enough space
                x = paddingLeft
                y += childHeight
            }

            child.layout(x, y, x + childWidth, y + childHeight)
            x += childWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var widthUsed = paddingLeft + paddingRight
        var heightUsed = paddingTop + paddingBottom
        var lineHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (widthUsed + childWidth > widthSize) {
                // Move to the next line if there's not enough space
                widthUsed = paddingLeft + paddingRight
                heightUsed += lineHeight
                lineHeight = 0
            }

            widthUsed += childWidth
            lineHeight = maxOf(lineHeight, childHeight)
        }

        val totalHeight = heightUsed + maxOf(lineHeight, suggestedMinimumHeight)
        setMeasuredDimension(widthSize, resolveSize(totalHeight, heightMeasureSpec))
    }
}
