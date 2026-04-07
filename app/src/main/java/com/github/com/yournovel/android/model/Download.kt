package com.github.com.yournovel.android.model

import android.content.Context
import com.github.com.yournovel.android.constant.IntentAction
import com.github.com.yournovel.android.service.DownloadService
import com.github.com.yournovel.android.utils.startService

object Download {


    fun start(context: Context, url: String, fileName: String) {
        context.startService<DownloadService> {
            action = IntentAction.start
            putExtra("url", url)
            putExtra("fileName", fileName)
        }
    }

}