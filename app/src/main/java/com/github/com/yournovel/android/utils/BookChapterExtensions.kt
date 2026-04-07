package com.github.com.yournovel.android.utils

import com.github.com.yournovel.android.data.entities.BookChapter

fun BookChapter.internString() {
    title = title.intern()
    bookUrl = bookUrl.intern()
}
