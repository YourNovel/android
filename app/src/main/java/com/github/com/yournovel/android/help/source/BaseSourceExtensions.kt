package com.github.com.yournovel.android.help.source

import com.github.com.yournovel.android.constant.SourceType
import com.github.com.yournovel.android.data.entities.BaseSource
import com.github.com.yournovel.android.data.entities.BookSource
import com.github.com.yournovel.android.data.entities.RssSource
import com.github.com.yournovel.android.model.SharedJsScope
import org.mozilla.javascript.Scriptable
import kotlin.coroutines.CoroutineContext

fun BaseSource.getShareScope(coroutineContext: CoroutineContext? = null): Scriptable? {
    return SharedJsScope.getScope(jsLib, coroutineContext)
}

fun BaseSource.getSourceType(): Int {
    return when (this) {
        is BookSource -> SourceType.book
        is RssSource -> SourceType.rss
        else -> error("unknown source type: ${this::class.simpleName}.")
    }
}
