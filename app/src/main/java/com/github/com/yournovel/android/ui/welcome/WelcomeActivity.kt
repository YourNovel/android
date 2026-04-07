package com.github.com.yournovel.android.ui.welcome

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.core.view.postDelayed
import com.github.com.yournovel.android.base.BaseActivity
import com.github.com.yournovel.android.constant.PreferKey
import com.github.com.yournovel.android.constant.Theme
import com.github.com.yournovel.android.data.appDb
import com.github.com.yournovel.android.databinding.ActivityWelcomeBinding
import com.github.com.yournovel.android.help.config.AppConfig
import com.github.com.yournovel.android.help.config.ThemeConfig
import com.github.com.yournovel.android.lib.theme.accentColor
import com.github.com.yournovel.android.lib.theme.backgroundColor
import com.github.com.yournovel.android.ui.book.read.ReadBookActivity
import com.github.com.yournovel.android.ui.main.MainActivity
import com.github.com.yournovel.android.utils.BitmapUtils
import com.github.com.yournovel.android.utils.fullScreen
import com.github.com.yournovel.android.utils.getPrefBoolean
import com.github.com.yournovel.android.utils.getPrefString
import com.github.com.yournovel.android.utils.setStatusBarColorAuto
import com.github.com.yournovel.android.utils.startActivity
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding
import com.github.com.yournovel.android.utils.visible
import com.github.com.yournovel.android.utils.windowSize

open class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {

    override val binding by viewBinding(ActivityWelcomeBinding::inflate)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.ivBook.setColorFilter(accentColor)
        binding.vwTitleLine.setBackgroundColor(accentColor)
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
        } else {
            binding.root.postDelayed(600) { startMainActivity() }
        }
    }

    override fun setupSystemBar() {
        fullScreen()
        setStatusBarColorAuto(backgroundColor, true, fullScreen)
        upNavigationBarColor()
    }

    override fun upBackgroundImage() {
        if (getPrefBoolean(PreferKey.customWelcome)) {
            kotlin.runCatching {
                when (ThemeConfig.getTheme()) {
                    Theme.Dark -> getPrefString(PreferKey.welcomeImageDark)?.let { path ->
                        val size = windowManager.windowSize
                        BitmapUtils.decodeBitmap(path, size.widthPixels, size.heightPixels).let {
                            binding.tvLegado.visible(AppConfig.welcomeShowTextDark)
                            binding.ivBook.visible(AppConfig.welcomeShowIconDark)
                            binding.tvGzh.visible(AppConfig.welcomeShowTextDark)
                            window.decorView.background = BitmapDrawable(resources, it)
                            return
                        }
                    }

                    else -> getPrefString(PreferKey.welcomeImage)?.let { path ->
                        val size = windowManager.windowSize
                        BitmapUtils.decodeBitmap(path, size.widthPixels, size.heightPixels).let {
                            binding.tvLegado.visible(AppConfig.welcomeShowText)
                            binding.ivBook.visible(AppConfig.welcomeShowIcon)
                            binding.tvGzh.visible(AppConfig.welcomeShowText)
                            window.decorView.background = BitmapDrawable(resources, it)
                            return
                        }
                    }
                }
            }
        }
        super.upBackgroundImage()
    }

    private fun startMainActivity() {
        startActivity<MainActivity>()
        if (getPrefBoolean(PreferKey.defaultToRead) && appDb.bookDao.lastReadBook != null) {
            startActivity<ReadBookActivity>()
        }
        finish()
    }

}

class Launcher1 : WelcomeActivity()
class Launcher2 : WelcomeActivity()
class Launcher3 : WelcomeActivity()
class Launcher4 : WelcomeActivity()
class Launcher5 : WelcomeActivity()
class Launcher6 : WelcomeActivity()