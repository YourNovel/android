package com.github.com.yournovel.android.ui.book.info.edit

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.MutableLiveData
import com.github.com.yournovel.android.base.BaseViewModel
import com.github.com.yournovel.android.constant.AppLog
import com.github.com.yournovel.android.data.appDb
import com.github.com.yournovel.android.data.entities.Book
import com.github.com.yournovel.android.model.ReadBook

class BookInfoEditViewModel(application: Application) : BaseViewModel(application) {
    var book: Book? = null
    val bookData = MutableLiveData<Book>()

    fun loadBook(bookUrl: String) {
        execute {
            book = appDb.bookDao.getBook(bookUrl)
            book?.let {
                bookData.postValue(it)
            }
        }
    }

    fun saveBook(book: Book, success: (() -> Unit)?) {
        execute {
            if (ReadBook.book?.bookUrl == book.bookUrl) {
                ReadBook.book = book
            }
            appDb.bookDao.update(book)
        }.onSuccess {
            success?.invoke()
        }.onError {
            if (it is SQLiteConstraintException) {
                AppLog.put("书籍信息保存失败，存在相同书名作者书籍\n$it", it, true)
            } else {
                AppLog.put("书籍信息保存失败\n$it", it, true)
            }
        }
    }
}