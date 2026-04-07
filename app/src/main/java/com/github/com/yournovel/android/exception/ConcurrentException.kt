@file:Suppress("unused")

package com.github.com.yournovel.android.exception

/**
 * 并发限制
 */
class ConcurrentException(msg: String, val waitTime: Int) : NoStackTraceException(msg)