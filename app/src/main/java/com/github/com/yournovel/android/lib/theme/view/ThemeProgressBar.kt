package com.github.com.yournovel.android.lib.theme.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.github.com.yournovel.android.lib.theme.accentColor
import com.github.com.yournovel.android.utils.applyTint

class ThemeProgressBar(context: Context, attrs: AttributeSet) : ProgressBar(context, attrs) {

    init {
        if (!isInEditMode) {
            applyTint(context.accentColor)
        }
    }
}