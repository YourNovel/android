package com.github.com.yournovel.android.ui.main.explore

import android.app.Application
import com.github.com.yournovel.android.base.BaseViewModel
import com.github.com.yournovel.android.data.appDb
import com.github.com.yournovel.android.data.entities.BookSourcePart
import com.github.com.yournovel.android.help.config.SourceConfig
import com.github.com.yournovel.android.help.source.SourceHelp

class ExploreViewModel(application: Application) : BaseViewModel(application) {

    fun topSource(bookSource: BookSourcePart) {
        execute {
            val minXh = appDb.bookSourceDao.minOrder
            bookSource.customOrder = minXh - 1
            appDb.bookSourceDao.upOrder(bookSource)
        }
    }

    fun deleteSource(source: BookSourcePart) {
        execute {
            SourceHelp.deleteBookSource(source.bookSourceUrl)
        }
    }

}