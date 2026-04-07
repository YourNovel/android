package com.github.com.yournovel.android.ui.book.import.remote

import android.app.Application
import com.github.com.yournovel.android.base.BaseViewModel
import com.github.com.yournovel.android.data.appDb
import com.github.com.yournovel.android.data.entities.Server

class ServersViewModel(application: Application): BaseViewModel(application) {


    fun delete(server: Server) {
        execute {
            appDb.serverDao.delete(server)
        }
    }

}