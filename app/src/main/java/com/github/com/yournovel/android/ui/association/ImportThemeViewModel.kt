package com.github.com.yournovel.android.ui.association

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.BaseViewModel
import com.github.com.yournovel.android.constant.AppConst
import com.github.com.yournovel.android.constant.AppLog
import com.github.com.yournovel.android.exception.NoStackTraceException
import com.github.com.yournovel.android.help.config.ThemeConfig
import com.github.com.yournovel.android.help.http.decompressed
import com.github.com.yournovel.android.help.http.newCallResponseBody
import com.github.com.yournovel.android.help.http.okHttpClient
import com.github.com.yournovel.android.help.http.text
import com.github.com.yournovel.android.utils.GSON
import com.github.com.yournovel.android.utils.fromJsonArray
import com.github.com.yournovel.android.utils.fromJsonObject
import com.github.com.yournovel.android.utils.isAbsUrl
import com.github.com.yournovel.android.utils.isJsonArray
import com.github.com.yournovel.android.utils.isJsonObject
import com.github.com.yournovel.android.utils.isUri
import com.github.com.yournovel.android.utils.readText
import splitties.init.appCtx

class ImportThemeViewModel(app: Application) : BaseViewModel(app) {

    val errorLiveData = MutableLiveData<String>()
    val successLiveData = MutableLiveData<Int>()

    val allSources = arrayListOf<ThemeConfig.Config>()
    val checkSources = arrayListOf<ThemeConfig.Config?>()
    val selectStatus = arrayListOf<Boolean>()

    val isSelectAll: Boolean
        get() {
            selectStatus.forEach {
                if (!it) {
                    return false
                }
            }
            return true
        }

    val selectCount: Int
        get() {
            var count = 0
            selectStatus.forEach {
                if (it) {
                    count++
                }
            }
            return count
        }

    fun importSelect(finally: () -> Unit) {
        execute {
            selectStatus.forEachIndexed { index, b ->
                if (b) {
                    ThemeConfig.addConfig(allSources[index])
                }
            }
        }.onFinally {
            finally.invoke()
        }
    }

    fun importSource(text: String) {
        execute {
            importSourceAwait(text.trim())
        }.onError {
            errorLiveData.postValue("ImportError:${it.localizedMessage}")
            AppLog.put("ImportError:${it.localizedMessage}", it)
        }.onSuccess {
            comparisonSource()
        }
    }

    private suspend fun importSourceAwait(text: String) {
        when {
            text.isJsonObject() -> {
                GSON.fromJsonObject<ThemeConfig.Config>(text).getOrThrow().let {
                    allSources.add(it)
                }
            }

            text.isJsonArray() -> GSON.fromJsonArray<ThemeConfig.Config>(text).getOrThrow()
                .let { items ->
                    allSources.addAll(items)
                }

            text.isAbsUrl() -> {
                importSourceUrl(text)
            }

            text.isUri() -> {
                importSourceAwait(text.toUri().readText(appCtx))
            }

            else -> throw NoStackTraceException(context.getString(R.string.wrong_format))
        }
    }

    private suspend fun importSourceUrl(url: String) {
        okHttpClient.newCallResponseBody {
            if (url.endsWith("#requestWithoutUA")) {
                url(url.substringBeforeLast("#requestWithoutUA"))
                header(AppConst.UA_NAME, "null")
            } else {
                url(url)
            }
        }.decompressed().text().let {
            importSourceAwait(it)
        }
    }

    private fun comparisonSource() {
        execute {
            allSources.forEach { config ->
                val source = ThemeConfig.configList.find {
                    it.themeName == config.themeName
                }
                checkSources.add(source)
                selectStatus.add(source == null || source != config)
            }
            successLiveData.postValue(allSources.size)
        }
    }

}