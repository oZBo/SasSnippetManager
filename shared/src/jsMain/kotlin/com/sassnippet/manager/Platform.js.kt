package com.sassnippet.manager

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
    override val baseUrl: String get() = "https://sassnippetmanager-production.up.railway.app"
    override val apiKey: String get() = "hds1ZWHqs7ZU4zwi"
}

actual fun getPlatform(): Platform = JsPlatform()