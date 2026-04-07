package com.github.com.yournovel.android.ui.rss.read

import com.github.com.yournovel.android.data.entities.BaseSource
import com.github.com.yournovel.android.help.JsExtensions
import com.github.com.yournovel.android.ui.association.AddToBookshelfDialog
import com.github.com.yournovel.android.ui.book.search.SearchActivity
import com.github.com.yournovel.android.utils.showDialogFragment

@Suppress("unused")
class RssJsExtensions(private val activity: ReadRssActivity) : JsExtensions {

    override fun getSource(): BaseSource? {
        return activity.getSource()
    }

    fun searchBook(key: String) {
        SearchActivity.start(activity, key)
    }

    fun addBook(bookUrl: String) {
        activity.showDialogFragment(AddToBookshelfDialog(bookUrl))
    }

}
