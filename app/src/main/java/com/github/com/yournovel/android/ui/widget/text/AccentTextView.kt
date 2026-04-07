package com.github.com.yournovel.android.ui.widget.text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.lib.theme.accentColor
import com.github.com.yournovel.android.utils.getCompatColor

class AccentTextView(context: Context, attrs: AttributeSet?) :
    AppCompatTextView(context, attrs) {

    init {
        if (!isInEditMode) {
            setTextColor(context.accentColor)
        } else {
            setTextColor(context.getCompatColor(R.color.accent))
        }
    }

}
