package com.github.com.yournovel.android.ui.dict

import android.app.Application
import com.github.com.yournovel.android.base.BaseViewModel
import com.github.com.yournovel.android.data.appDb
import com.github.com.yournovel.android.data.entities.DictRule
import com.github.com.yournovel.android.help.coroutine.Coroutine

class DictViewModel(application: Application) : BaseViewModel(application) {

    private var dictJob: Coroutine<String>? = null

    fun initData(onSuccess: (List<DictRule>) -> Unit) {
        execute {
            appDb.dictRuleDao.enabled
        }.onSuccess {
            onSuccess.invoke(it)
        }
    }

    fun dict(
        dictRule: DictRule,
        word: String,
        onFinally: (String) -> Unit
    ) {
        dictJob?.cancel()
        dictJob = execute {
            dictRule.search(word)
        }.onSuccess {
            onFinally.invoke(it)
        }.onError {
            onFinally.invoke(it.localizedMessage ?: "ERROR")
        }
    }


}