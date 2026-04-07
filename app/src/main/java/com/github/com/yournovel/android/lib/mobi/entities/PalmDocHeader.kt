package com.github.com.yournovel.android.lib.mobi.entities

data class PalmDocHeader(
    val compression: Int,
    val numTextRecords: Int,
    val recordSize: Int,
    val encryption: Int
)
