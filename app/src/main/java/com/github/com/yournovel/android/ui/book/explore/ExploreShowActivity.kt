package com.github.com.yournovel.android.ui.book.explore

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.VMBaseActivity
import com.github.com.yournovel.android.data.entities.SearchBook
import com.github.com.yournovel.android.databinding.ActivityExploreShowBinding
import com.github.com.yournovel.android.databinding.ViewLoadMoreBinding
import com.github.com.yournovel.android.ui.book.info.BookInfoActivity
import com.github.com.yournovel.android.ui.widget.recycler.LoadMoreView
import com.github.com.yournovel.android.ui.widget.recycler.VerticalDivider
import com.github.com.yournovel.android.utils.applyNavigationBarPadding
import com.github.com.yournovel.android.utils.startActivity
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding

/**
 * 发现列表
 */
class ExploreShowActivity : VMBaseActivity<ActivityExploreShowBinding, ExploreShowViewModel>(),
    ExploreShowAdapter.CallBack {
    override val binding by viewBinding(ActivityExploreShowBinding::inflate)
    override val viewModel by viewModels<ExploreShowViewModel>()

    private val adapter by lazy { ExploreShowAdapter(this, this) }
    private val loadMoreView by lazy { LoadMoreView(this) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.titleBar.title = intent.getStringExtra("exploreName")
        initRecyclerView()
        viewModel.booksData.observe(this) { upData(it) }
        viewModel.initData(intent)
        viewModel.errorLiveData.observe(this) {
            loadMoreView.error(it)
        }
        viewModel.upAdapterLiveData.observe(this) {
            adapter.notifyItemRangeChanged(0, adapter.itemCount, bundleOf(it to null))
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.addItemDecoration(VerticalDivider(this))
        binding.recyclerView.adapter = adapter
        binding.recyclerView.applyNavigationBarPadding()
        adapter.addFooterView {
            ViewLoadMoreBinding.bind(loadMoreView)
        }
        loadMoreView.startLoad()
        loadMoreView.setOnClickListener {
            if (!loadMoreView.isLoading) {
                scrollToBottom(true)
            }
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    scrollToBottom()
                }
            }
        })
    }

    private fun scrollToBottom(forceLoad: Boolean = false) {
        if ((loadMoreView.hasMore && !loadMoreView.isLoading) || forceLoad) {
            loadMoreView.hasMore()
            viewModel.explore()
        }
    }

    private fun upData(books: List<SearchBook>) {
        loadMoreView.stopLoad()
        if (books.isEmpty() && adapter.isEmpty()) {
            loadMoreView.noMore(getString(R.string.empty))
        } else if (adapter.getActualItemCount() == books.size) {
            loadMoreView.noMore()
        } else {
            adapter.setItems(books)
        }
    }

    override fun isInBookshelf(book: SearchBook): Boolean {
        return viewModel.isInBookShelf(book)
    }

    override fun showBookInfo(book: SearchBook) {
        startActivity<BookInfoActivity> {
            putExtra("name", book.name)
            putExtra("author", book.author)
            putExtra("bookUrl", book.bookUrl)
        }
    }
}
