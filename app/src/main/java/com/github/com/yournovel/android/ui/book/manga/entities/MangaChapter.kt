package com.github.com.yournovel.android.ui.book.manga.entities

import com.github.com.yournovel.android.data.entities.BookChapter

data class MangaChapter(
    val chapter: BookChapter,
    val pages: List<BaseMangaPage>,
    val imageCount: Int
)
