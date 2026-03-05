package com.sassnippet.manager

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val baseUrl: String get() = "http://10.0.2.2:8080"
    override val apiKey: String get() = "dev-secret-key"
}

actual fun getPlatform(): Platform = AndroidPlatform()