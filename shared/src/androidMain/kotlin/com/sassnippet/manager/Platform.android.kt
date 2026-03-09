package com.sassnippet.manager

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val baseUrl: String get() = "https://sassnippetmanager-production.up.railway.app"
    override val apiKey: String get() = "hds1ZWHqs7ZU4zwi"
}

actual fun getPlatform(): Platform = AndroidPlatform()