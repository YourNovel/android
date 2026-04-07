package com.github.com.yournovel.android.lib.theme.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.github.com.yournovel.android.lib.theme.accentColor
import com.github.com.yournovel.android.utils.applyTint

/**
 * @author Aidan Follestad (afollestad)
 */
class ThemeSeekBar(context: Context, attrs: AttributeSet) : AppCompatSeekBar(context, attrs) {

    init {
        if (!isInEditMode) {
            applyTint(context.accentColor)
        }
    }
}
