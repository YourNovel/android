package com.github.com.yournovel.android.lib.permission

interface OnPermissionsResultCallback {

    fun onPermissionsGranted()

    fun onPermissionsDenied(deniedPermissions: Array<String>?)

    fun onError(e: Exception)

}