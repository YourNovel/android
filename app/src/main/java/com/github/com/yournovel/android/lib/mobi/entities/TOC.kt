package com.github.com.yournovel.android.lib.mobi.entities

data class TOC(
    val label: String,
    val href: String,
    val subitems: List<TOC>? = null
)
