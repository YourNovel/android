package com.github.com.yournovel.android.ui.qrcode

import com.google.zxing.Result

interface ScanResultCallback {

    fun onScanResultCallback(result: Result?)

}