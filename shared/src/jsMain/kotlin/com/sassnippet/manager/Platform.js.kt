package com.sassnippet.manager

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
    override val baseUrl: String get() = ""
}

actual fun getPlatform(): Platform = JsPlatform()