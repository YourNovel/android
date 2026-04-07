package com.github.com.yournovel.android.ui.config

import android.app.Application
import android.content.Context
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.BaseViewModel
import com.github.com.yournovel.android.data.appDb
import com.github.com.yournovel.android.help.AppWebDav
import com.github.com.yournovel.android.help.book.BookHelp
import com.github.com.yournovel.android.utils.FileUtils
import com.github.com.yournovel.android.utils.restart
import com.github.com.yournovel.android.utils.toastOnUi
import kotlinx.coroutines.delay
import splitties.init.appCtx

class ConfigViewModel(application: Application) : BaseViewModel(application) {

    fun upWebDavConfig() {
        execute {
            AppWebDav.upConfig()
        }
    }

    fun clearCache() {
        execute {
            BookHelp.clearCache()
            FileUtils.delete(context.cacheDir.absolutePath)
        }.onSuccess {
            context.toastOnUi(R.string.clear_cache_success)
        }
    }

    fun clearWebViewData() {
        execute {
            FileUtils.delete(context.getDir("webview", Context.MODE_PRIVATE))
            FileUtils.delete(context.getDir("hws_webview", Context.MODE_PRIVATE), true)
            context.toastOnUi(R.string.clear_webview_data_success)
            delay(3000)
            appCtx.restart()
        }
    }

    fun shrinkDatabase() {
        execute {
            appDb.openHelper.writableDatabase.execSQL("VACUUM")
        }.onSuccess {
            context.toastOnUi(R.string.success)
        }
    }

}
