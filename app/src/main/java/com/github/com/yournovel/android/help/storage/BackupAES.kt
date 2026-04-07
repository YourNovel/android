package com.github.com.yournovel.android.help.storage

import cn.hutool.crypto.symmetric.AES
import com.github.com.yournovel.android.help.config.LocalConfig
import com.github.com.yournovel.android.utils.MD5Utils

class BackupAES : AES(
    MD5Utils.md5Encode(LocalConfig.password ?: "").encodeToByteArray(0, 16)
)