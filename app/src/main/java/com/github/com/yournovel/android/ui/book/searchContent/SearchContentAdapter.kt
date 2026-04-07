package com.github.com.yournovel.android.ui.book.searchContent

import android.content.Context
import android.view.ViewGroup
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.adapter.ItemViewHolder
import com.github.com.yournovel.android.base.adapter.RecyclerAdapter
import com.github.com.yournovel.android.databinding.ItemSearchListBinding
import com.github.com.yournovel.android.lib.theme.accentColor
import com.github.com.yournovel.android.utils.getCompatColor
import com.github.com.yournovel.android.utils.hexString


class SearchContentAdapter(context: Context, val callback: Callback) :
    RecyclerAdapter<SearchResult, ItemSearchListBinding>(context) {

    val textColor = context.getCompatColor(R.color.primaryText).hexString.substring(2)
    val accentColor = context.accentColor.hexString.substring(2)

    override fun getViewBinding(parent: ViewGroup): ItemSearchListBinding {
        return ItemSearchListBinding.inflate(inflater, parent, false)
    }

    override fun convert(
        holder: ItemViewHolder,
        binding: ItemSearchListBinding,
        item: SearchResult,
        payloads: MutableList<Any>
    ) {
        binding.run {
            val isDur = callback.durChapterIndex() == item.chapterIndex
            if (payloads.isEmpty()) {
                tvSearchResult.text = item.getHtmlCompat(textColor, accentColor)
                tvSearchResult.paint.isFakeBoldText = isDur
            }
        }
    }

    override fun registerListener(holder: ItemViewHolder, binding: ItemSearchListBinding) {
        holder.itemView.setOnClickListener {
            getItem(holder.layoutPosition)?.let {
                if (it.query.isNotBlank()) callback.openSearchResult(it, holder.layoutPosition)
            }
        }
    }

    interface Callback {
        fun openSearchResult(searchResult: SearchResult, index: Int)
        fun durChapterIndex(): Int
    }
}