package com.aghajari.autoanimate.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class ClipFrameLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    init {
        clipToOutline = true
    }
}