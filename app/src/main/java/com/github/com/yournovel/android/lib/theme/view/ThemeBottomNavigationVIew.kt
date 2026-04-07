package com.github.com.yournovel.android.lib.theme.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.github.com.yournovel.android.databinding.ViewNavigationBadgeBinding
import com.github.com.yournovel.android.help.config.AppConfig
import com.github.com.yournovel.android.lib.theme.Selector
import com.github.com.yournovel.android.lib.theme.ThemeStore
import com.github.com.yournovel.android.lib.theme.bottomBackground
import com.github.com.yournovel.android.lib.theme.getSecondaryTextColor
import com.github.com.yournovel.android.ui.widget.text.BadgeView
import com.github.com.yournovel.android.utils.ColorUtils

class ThemeBottomNavigationVIew(context: Context, attrs: AttributeSet) :
    BottomNavigationView(context, attrs) {

    init {
        val bgColor = context.bottomBackground
        setBackgroundColor(bgColor)
        val textIsDark = ColorUtils.isColorLight(bgColor)
        val textColor = context.getSecondaryTextColor(textIsDark)
        val colorStateList = Selector.colorBuild()
            .setDefaultColor(textColor)
            .setSelectedColor(ThemeStore.accentColor(context)).create()
        itemIconTintList = colorStateList
        itemTextColor = colorStateList

        if (AppConfig.isEInkMode) {
            isItemHorizontalTranslationEnabled = false
            itemBackground = ColorDrawable(Color.TRANSPARENT)
        }

        ViewCompat.setOnApplyWindowInsetsListener(this, null)
    }

    fun addBadgeView(index: Int): BadgeView {
        //获取底部菜单view
        val menuView = getChildAt(0) as ViewGroup
        //获取第index个itemView
        val itemView = menuView.getChildAt(index) as ViewGroup
        val badgeBinding = ViewNavigationBadgeBinding.inflate(LayoutInflater.from(context))
        itemView.addView(badgeBinding.root)
        return badgeBinding.viewBadge
    }

}