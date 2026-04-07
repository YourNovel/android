package com.github.com.yournovel.android.help.http

import com.github.com.yournovel.android.lib.cronet.CronetInterceptor
import com.github.com.yournovel.android.lib.cronet.CronetLoader
import okhttp3.Interceptor

object Cronet {

    val loader: LoaderInterface? by lazy {
        CronetLoader
    }

    fun preDownload() {
        loader?.preDownload()
    }

    val interceptor: Interceptor? by lazy {
        CronetInterceptor(cookieJar)
    }

    interface LoaderInterface {

        fun install(): Boolean

        fun preDownload()

    }

}