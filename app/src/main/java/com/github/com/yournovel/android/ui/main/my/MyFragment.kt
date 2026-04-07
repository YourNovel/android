package com.github.com.yournovel.android.ui.main.my

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.preference.Preference
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.BaseFragment
import com.github.com.yournovel.android.constant.EventBus
import com.github.com.yournovel.android.constant.PreferKey
import com.github.com.yournovel.android.databinding.FragmentMyConfigBinding
import com.github.com.yournovel.android.help.config.ThemeConfig
import com.github.com.yournovel.android.lib.dialogs.selector
import com.github.com.yournovel.android.lib.prefs.NameListPreference
import com.github.com.yournovel.android.lib.prefs.SwitchPreference
import com.github.com.yournovel.android.lib.prefs.fragment.PreferenceFragment
import com.github.com.yournovel.android.lib.theme.primaryColor
import com.github.com.yournovel.android.service.WebService
import com.github.com.yournovel.android.ui.about.AboutActivity
import com.github.com.yournovel.android.ui.about.ReadRecordActivity
import com.github.com.yournovel.android.ui.book.bookmark.AllBookmarkActivity
import com.github.com.yournovel.android.ui.book.source.manage.BookSourceActivity
import com.github.com.yournovel.android.ui.book.toc.rule.TxtTocRuleActivity
import com.github.com.yournovel.android.ui.config.ConfigActivity
import com.github.com.yournovel.android.ui.config.ConfigTag
import com.github.com.yournovel.android.ui.dict.rule.DictRuleActivity
import com.github.com.yournovel.android.ui.file.FileManageActivity
import com.github.com.yournovel.android.ui.main.MainFragmentInterface
import com.github.com.yournovel.android.ui.replace.ReplaceRuleActivity
import com.github.com.yournovel.android.utils.LogUtils
import com.github.com.yournovel.android.utils.getPrefBoolean
import com.github.com.yournovel.android.utils.observeEventSticky
import com.github.com.yournovel.android.utils.openUrl
import com.github.com.yournovel.android.utils.putPrefBoolean
import com.github.com.yournovel.android.utils.sendToClip
import com.github.com.yournovel.android.utils.setEdgeEffectColor
import com.github.com.yournovel.android.utils.showHelp
import com.github.com.yournovel.android.utils.startActivity
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding

class MyFragment() : BaseFragment(R.layout.fragment_my_config), MainFragmentInterface {

    constructor(position: Int) : this() {
        val bundle = Bundle()
        bundle.putInt("position", position)
        arguments = bundle
    }

    override val position: Int? get() = arguments?.getInt("position")

    private val binding by viewBinding(FragmentMyConfigBinding::bind)

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setSupportToolbar(binding.titleBar.toolbar)
        val fragmentTag = "prefFragment"
        var preferenceFragment = childFragmentManager.findFragmentByTag(fragmentTag)
        if (preferenceFragment == null) preferenceFragment = MyPreferenceFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.pre_fragment, preferenceFragment, fragmentTag).commit()
    }

    override fun onCompatCreateOptionsMenu(menu: Menu) {
        menuInflater.inflate(R.menu.main_my, menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_help -> showHelp("appHelp")
        }
    }

    /**
     * 配置
     */
    class MyPreferenceFragment : PreferenceFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            putPrefBoolean(PreferKey.webService, WebService.isRun)
            addPreferencesFromResource(R.xml.pref_main)
            findPreference<SwitchPreference>("webService")?.onLongClick {
                if (!WebService.isRun) {
                    return@onLongClick false
                }
                context?.selector(arrayListOf("复制地址", "浏览器打开")) { _, i ->
                    when (i) {
                        0 -> context?.sendToClip(it.summary.toString())
                        1 -> context?.openUrl(it.summary.toString())
                    }
                }
                true
            }
            observeEventSticky<String>(EventBus.WEB_SERVICE) {
                findPreference<SwitchPreference>(PreferKey.webService)?.let {
                    it.isChecked = WebService.isRun
                    it.summary = if (WebService.isRun) {
                        WebService.hostAddress
                    } else {
                        getString(R.string.web_service_desc)
                    }
                }
            }
            findPreference<NameListPreference>(PreferKey.themeMode)?.let {
                it.setOnPreferenceChangeListener { _, _ ->
                    view?.post { ThemeConfig.applyDayNight(requireContext()) }
                    true
                }
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.setEdgeEffectColor(primaryColor)
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
            super.onPause()
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            when (key) {
                PreferKey.webService -> {
                    if (requireContext().getPrefBoolean("webService")) {
                        WebService.start(requireContext())
                    } else {
                        WebService.stop(requireContext())
                    }
                }

                "recordLog" -> LogUtils.upLevel()
            }
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                "bookSourceManage" -> startActivity<BookSourceActivity>()
                "replaceManage" -> startActivity<ReplaceRuleActivity>()
                "dictRuleManage" -> startActivity<DictRuleActivity>()
                "txtTocRuleManage" -> startActivity<TxtTocRuleActivity>()
                "bookmark" -> startActivity<AllBookmarkActivity>()
                "setting" -> startActivity<ConfigActivity> {
                    putExtra("configTag", ConfigTag.OTHER_CONFIG)
                }

                "web_dav_setting" -> startActivity<ConfigActivity> {
                    putExtra("configTag", ConfigTag.BACKUP_CONFIG)
                }

                "theme_setting" -> startActivity<ConfigActivity> {
                    putExtra("configTag", ConfigTag.THEME_CONFIG)
                }

                "fileManage" -> startActivity<FileManageActivity>()
                "readRecord" -> startActivity<ReadRecordActivity>()
                "about" -> startActivity<AboutActivity>()
                "exit" -> activity?.finish()
            }
            return super.onPreferenceTreeClick(preference)
        }


    }
}