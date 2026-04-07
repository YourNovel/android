package com.github.com.yournovel.android.ui.browser

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.view.size
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.VMBaseActivity
import com.github.com.yournovel.android.constant.AppConst
import com.github.com.yournovel.android.constant.AppConst.imagePathKey
import com.github.com.yournovel.android.databinding.ActivityWebViewBinding
import com.github.com.yournovel.android.help.config.AppConfig
import com.github.com.yournovel.android.help.http.CookieStore
import com.github.com.yournovel.android.help.source.SourceVerificationHelp
import com.github.com.yournovel.android.lib.dialogs.SelectItem
import com.github.com.yournovel.android.lib.dialogs.alert
import com.github.com.yournovel.android.lib.dialogs.selector
import com.github.com.yournovel.android.lib.theme.accentColor
import com.github.com.yournovel.android.model.Download
import com.github.com.yournovel.android.ui.association.OnLineImportActivity
import com.github.com.yournovel.android.ui.file.HandleFileContract
import com.github.com.yournovel.android.utils.ACache
import com.github.com.yournovel.android.utils.gone
import com.github.com.yournovel.android.utils.invisible
import com.github.com.yournovel.android.utils.keepScreenOn
import com.github.com.yournovel.android.utils.longSnackbar
import com.github.com.yournovel.android.utils.openUrl
import com.github.com.yournovel.android.utils.sendToClip
import com.github.com.yournovel.android.utils.setDarkeningAllowed
import com.github.com.yournovel.android.utils.startActivity
import com.github.com.yournovel.android.utils.toggleSystemBar
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding
import com.github.com.yournovel.android.utils.visible
import java.net.URLDecoder
import com.github.com.yournovel.android.help.http.CookieManager as AppCookieManager

class WebViewActivity : VMBaseActivity<ActivityWebViewBinding, WebViewModel>() {

    override val binding by viewBinding(ActivityWebViewBinding::inflate)
    override val viewModel by viewModels<WebViewModel>()
    private var customWebViewCallback: WebChromeClient.CustomViewCallback? = null
    private var webPic: String? = null
    private var isCloudflareChallenge = false
    private var isFullScreen = false
    private val saveImage = registerForActivityResult(HandleFileContract()) {
        it.uri?.let { uri ->
            ACache.get().put(imagePathKey, uri.toString())
            viewModel.saveImage(webPic, uri.toString())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.titleBar.title = intent.getStringExtra("title") ?: getString(R.string.loading)
        binding.titleBar.subtitle = intent.getStringExtra("sourceName")
        viewModel.initData(intent) {
            val url = viewModel.baseUrl
            val headerMap = viewModel.headerMap
            initWebView(url, headerMap)
            val html = viewModel.html
            if (html.isNullOrEmpty()) {
                binding.webView.loadUrl(url, headerMap)
            } else {
                binding.webView.loadDataWithBaseURL(url, html, "text/html", "utf-8", url)
            }
        }
        onBackPressedDispatcher.addCallback(this) {
            if (binding.customWebView.size > 0) {
                customWebViewCallback?.onCustomViewHidden()
                return@addCallback
            } else if (binding.webView.canGoBack()
                && binding.webView.copyBackForwardList().size > 1
            ) {
                binding.webView.goBack()
                return@addCallback
            }
            if (isFullScreen) {
                toggleFullScreen()
                return@addCallback
            }
            finish()
        }
    }

    override fun onCompatCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.web_view, menu)
        return super.onCompatCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (viewModel.sourceOrigin.isNotEmpty()) {
            menu.findItem(R.id.menu_disable_source)?.isVisible = true
            menu.findItem(R.id.menu_delete_source)?.isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_open_in_browser -> openUrl(viewModel.baseUrl)
            R.id.menu_copy_url -> sendToClip(viewModel.baseUrl)
            R.id.menu_ok -> {
                if (viewModel.sourceVerificationEnable) {
                    viewModel.saveVerificationResult(binding.webView) {
                        finish()
                    }
                } else {
                    finish()
                }
            }

            R.id.menu_full_screen -> toggleFullScreen()
            R.id.menu_disable_source -> {
                viewModel.disableSource {
                    finish()
                }
            }

            R.id.menu_delete_source -> {
                alert(R.string.draw) {
                    setMessage(getString(R.string.sure_del) + "\n" + viewModel.sourceName)
                    noButton()
                    yesButton {
                        viewModel.deleteSource {
                            finish()
                        }
                    }
                }
            }
        }
        return super.onCompatOptionsItemSelected(item)
    }

    //实现starBrowser调起页面全屏
    private fun toggleFullScreen() {
        isFullScreen = !isFullScreen

        toggleSystemBar(!isFullScreen)

        if (isFullScreen) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String, headerMap: HashMap<String, String>) {
        binding.progressBar.fontColor = accentColor
        binding.webView.webChromeClient = CustomWebChromeClient()
        binding.webView.webViewClient = CustomWebViewClient()
        binding.webView.settings.apply {
            setDarkeningAllowed(AppConfig.isNightTheme)
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            domStorageEnabled = true
            allowContentAccess = true
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            headerMap[AppConst.UA_NAME]?.let {
                userAgentString = it
            }
        }
        AppCookieManager.applyToWebView(url)
        binding.webView.setOnLongClickListener {
            val hitTestResult = binding.webView.hitTestResult
            if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
            ) {
                hitTestResult.extra?.let { webPic ->
                    selector(
                        arrayListOf(
                            SelectItem(getString(R.string.action_save), "save"),
                            SelectItem(getString(R.string.select_folder), "selectFolder")
                        )
                    ) { _, charSequence, _ ->
                        when (charSequence.value) {
                            "save" -> saveImage(webPic)
                            "selectFolder" -> selectSaveFolder()
                        }
                    }
                    return@setOnLongClickListener true
                }
            }
            return@setOnLongClickListener false
        }
        binding.webView.setDownloadListener { downloadUrl, _, contentDisposition, _, _ ->
            var fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, null)
            fileName = URLDecoder.decode(fileName, "UTF-8")
            binding.llView.longSnackbar(fileName, getString(R.string.action_download)) {
                Download.start(this, downloadUrl, fileName)
            }
        }
    }

    private fun saveImage(webPic: String) {
        this.webPic = webPic
        val path = ACache.get().getAsString(imagePathKey)
        if (path.isNullOrEmpty()) {
            selectSaveFolder()
        } else {
            viewModel.saveImage(webPic, path)
        }
    }

    private fun selectSaveFolder() {
        val default = arrayListOf<SelectItem<Int>>()
        val path = ACache.get().getAsString(imagePathKey)
        if (!path.isNullOrEmpty()) {
            default.add(SelectItem(path, -1))
        }
        saveImage.launch {
            otherActions = default
        }
    }

    override fun finish() {
        SourceVerificationHelp.checkResult(viewModel.sourceOrigin)
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }

    inner class CustomWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.progressBar.setDurProgress(newProgress)
            binding.progressBar.gone(newProgress == 100)
        }

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            binding.llView.invisible()
            binding.customWebView.addView(view)
            customWebViewCallback = callback
            keepScreenOn(true)
            toggleSystemBar(false)
        }

        override fun onHideCustomView() {
            binding.customWebView.removeAllViews()
            binding.llView.visible()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            keepScreenOn(false)
            toggleSystemBar(true)
        }
    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            request?.let {
                return shouldOverrideUrlLoading(it.url)
            }
            return true
        }

        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION", "KotlinRedundantDiagnosticSuppress")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            url?.let {
                return shouldOverrideUrlLoading(Uri.parse(it))
            }
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val cookieManager = CookieManager.getInstance()
            url?.let {
                CookieStore.setCookie(it, cookieManager.getCookie(it))
            }
            view?.title?.let { title ->
                if (title != url && title != view.url && title.isNotBlank()) {
                    binding.titleBar.title = title
                } else {
                    binding.titleBar.title = intent.getStringExtra("title")
                }
                view.evaluateJavascript("!!window._cf_chl_opt") {
                    if (it == "true") {
                        isCloudflareChallenge = true
                    } else if (isCloudflareChallenge && viewModel.sourceVerificationEnable) {
                        viewModel.saveVerificationResult(binding.webView) {
                            finish()
                        }
                    }
                }
            }
        }

        private fun shouldOverrideUrlLoading(url: Uri): Boolean {
            when (url.scheme) {
                "http", "https" -> {
                    return false
                }

                "legado", "yuedu" -> {
                    startActivity<OnLineImportActivity> {
                        data = url
                    }
                    return true
                }

                else -> {
                    binding.root.longSnackbar(R.string.jump_to_another_app, R.string.confirm) {
                        openUrl(url)
                    }
                    return true
                }
            }
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed()
        }

    }

}