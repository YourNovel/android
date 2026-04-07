package com.github.com.yournovel.android.model.remote

import android.net.Uri
import com.github.com.yournovel.android.constant.AppPattern.archiveFileRegex
import com.github.com.yournovel.android.constant.AppPattern.bookFileRegex
import com.github.com.yournovel.android.constant.BookType
import com.github.com.yournovel.android.data.entities.Book
import com.github.com.yournovel.android.exception.NoStackTraceException
import com.github.com.yournovel.android.help.book.update
import com.github.com.yournovel.android.help.config.AppConfig
import com.github.com.yournovel.android.lib.webdav.Authorization
import com.github.com.yournovel.android.lib.webdav.WebDav
import com.github.com.yournovel.android.lib.webdav.WebDavFile
import com.github.com.yournovel.android.model.analyzeRule.CustomUrl
import com.github.com.yournovel.android.model.localBook.LocalBook
import com.github.com.yournovel.android.utils.NetworkUtils
import com.github.com.yournovel.android.utils.isContentScheme
import kotlinx.coroutines.runBlocking

class RemoteBookWebDav(
    val rootBookUrl: String,
    val authorization: Authorization,
    val serverID: Long? = null
) : RemoteBookManager() {

    init {
        runBlocking {
            WebDav(rootBookUrl, authorization).makeAsDir()
        }
    }


    @Throws(Exception::class)
    override suspend fun getRemoteBookList(path: String): MutableList<RemoteBook> {
        if (!NetworkUtils.isAvailable()) throw NoStackTraceException("网络不可用")
        val remoteBooks = mutableListOf<RemoteBook>()
        //读取文件列表
        val remoteWebDavFileList: List<WebDavFile> = WebDav(path, authorization).listFiles()
        //转化远程文件信息到本地对象
        remoteWebDavFileList.forEach { webDavFile ->
            if (webDavFile.isDir
                || bookFileRegex.matches(webDavFile.displayName)
                || archiveFileRegex.matches(webDavFile.displayName)
            ) {
                //扩展名符合阅读的格式则认为是书籍
                remoteBooks.add(RemoteBook(webDavFile))
            }
        }
        return remoteBooks
    }

    override suspend fun getRemoteBook(path: String): RemoteBook? {
        if (!NetworkUtils.isAvailable()) throw NoStackTraceException("网络不可用")
        val webDavFile = WebDav(path, authorization).getWebDavFile()
            ?: return null
        return RemoteBook(webDavFile)
    }

    override suspend fun downloadRemoteBook(remoteBook: RemoteBook): Uri {
        AppConfig.defaultBookTreeUri
            ?: throw NoStackTraceException("没有设置书籍保存位置!")
        if (!NetworkUtils.isAvailable()) throw NoStackTraceException("网络不可用")
        val webdav = WebDav(remoteBook.path, authorization)
        return webdav.downloadInputStream().let { inputStream ->
            LocalBook.saveBookFile(inputStream, remoteBook.filename)
        }
    }

    override suspend fun upload(book: Book) {
        if (!NetworkUtils.isAvailable()) throw NoStackTraceException("网络不可用")
        val localBookUri = Uri.parse(book.bookUrl)
        val putUrl = "$rootBookUrl${book.originName}"
        val webDav = WebDav(putUrl, authorization)
        if (localBookUri.isContentScheme()) {
            webDav.upload(localBookUri)
        } else {
            webDav.upload(localBookUri.path!!)
        }
        book.origin = BookType.webDavTag + CustomUrl(putUrl)
            .putAttribute("serverID", serverID)
            .toString()
        book.update()
    }

    override suspend fun delete(remoteBookUrl: String) {
        if (!NetworkUtils.isAvailable()) throw NoStackTraceException("网络不可用")
        WebDav(remoteBookUrl, authorization).delete()
    }

}
