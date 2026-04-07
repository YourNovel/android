package com.github.com.yournovel.android.exception

import com.github.com.yournovel.android.R
import splitties.init.appCtx

class NoBooksDirException: NoStackTraceException(appCtx.getString(R.string.no_books_dir))