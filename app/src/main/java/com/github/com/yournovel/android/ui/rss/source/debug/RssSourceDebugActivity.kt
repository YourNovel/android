package com.github.com.yournovel.android.ui.rss.source.debug

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.VMBaseActivity
import com.github.com.yournovel.android.databinding.ActivitySourceDebugBinding
import com.github.com.yournovel.android.lib.theme.accentColor
import com.github.com.yournovel.android.lib.theme.primaryColor
import com.github.com.yournovel.android.ui.widget.dialog.TextDialog
import com.github.com.yournovel.android.utils.applyNavigationBarPadding
import com.github.com.yournovel.android.utils.gone
import com.github.com.yournovel.android.utils.setEdgeEffectColor
import com.github.com.yournovel.android.utils.showDialogFragment
import com.github.com.yournovel.android.utils.toastOnUi
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch


class RssSourceDebugActivity : VMBaseActivity<ActivitySourceDebugBinding, RssSourceDebugModel>() {

    override val binding by viewBinding(ActivitySourceDebugBinding::inflate)
    override val viewModel by viewModels<RssSourceDebugModel>()

    private val adapter by lazy { RssSourceDebugAdapter(this) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initRecyclerView()
        initSearchView()
        viewModel.observe { state, msg ->
            lifecycleScope.launch {
                adapter.addItem(msg)
                if (state == -1 || state == 1000) {
                    binding.rotateLoading.gone()
                }
            }
        }
        viewModel.initData(intent.getStringExtra("key")) {
            startDebug()
        }
    }

    override fun onCompatCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rss_source_debug, menu)
        return super.onCompatCreateOptionsMenu(menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_list_src -> showDialogFragment(TextDialog("Html", viewModel.listSrc))
            R.id.menu_content_src -> showDialogFragment(TextDialog("Html", viewModel.contentSrc))
        }
        return super.onCompatOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        binding.recyclerView.setEdgeEffectColor(primaryColor)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.applyNavigationBarPadding()
        binding.rotateLoading.loadingColor = accentColor
    }

    private fun initSearchView() {
        binding.titleBar.findViewById<SearchView>(R.id.search_view).gone()
    }

    private fun startDebug() {
        adapter.clearItems()
        viewModel.rssSource?.let {
            binding.rotateLoading.visible()
            viewModel.startDebug(it)
        } ?: toastOnUi(R.string.error_no_source)
    }
}