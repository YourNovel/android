package com.github.com.yournovel.android.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.VMBaseActivity
import com.github.com.yournovel.android.data.entities.BaseSource
import com.github.com.yournovel.android.databinding.ActivitySourceLoginBinding
import com.github.com.yournovel.android.utils.showDialogFragment
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding


class SourceLoginActivity : VMBaseActivity<ActivitySourceLoginBinding, SourceLoginViewModel>() {

    override val binding by viewBinding(ActivitySourceLoginBinding::inflate)
    override val viewModel by viewModels<SourceLoginViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel.initData(intent, success = { source ->
            initView(source)
        }, error = {
            finish()
        })
    }

    private fun initView(source: BaseSource) {
        if (source.loginUi.isNullOrEmpty()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment, WebViewLoginFragment(), "webViewLogin")
                .commit()
        } else {
            showDialogFragment<SourceLoginDialog>()
        }
    }

}