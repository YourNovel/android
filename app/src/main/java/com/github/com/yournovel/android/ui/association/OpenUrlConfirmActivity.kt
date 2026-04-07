package com.github.com.yournovel.android.ui.association

import android.os.Bundle
import com.github.com.yournovel.android.base.BaseActivity
import com.github.com.yournovel.android.constant.SourceType
import com.github.com.yournovel.android.databinding.ActivityTranslucenceBinding
import com.github.com.yournovel.android.utils.showDialogFragment
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding

class OpenUrlConfirmActivity :
    BaseActivity<ActivityTranslucenceBinding>() {

    override val binding by viewBinding(ActivityTranslucenceBinding::inflate)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        intent.getStringExtra("uri")?.let {
            val mimeType = intent.getStringExtra("mimeType")
            val sourceOrigin = intent.getStringExtra("sourceOrigin")
            val sourceName = intent.getStringExtra("sourceName")
            val sourceType = intent.getIntExtra("sourceType", SourceType.book)
            showDialogFragment(OpenUrlConfirmDialog(it, mimeType, sourceOrigin, sourceName, sourceType))
        } ?: finish()
    }

}
