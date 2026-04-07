package com.github.com.yournovel.android.utils.canvasrecorder.pools

import android.graphics.Picture
import com.github.com.yournovel.android.utils.objectpool.BaseObjectPool

class PicturePool : BaseObjectPool<Picture>(64) {

    override fun create(): Picture = Picture()

}
