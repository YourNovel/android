package com.github.com.yournovel.android.help

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val globalExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
