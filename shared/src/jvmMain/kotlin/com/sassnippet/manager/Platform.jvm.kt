package com.sassnippet.manager

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val baseUrl: String get() = ""
    override val apiKey: String get() = "dev-secret-key"
}

actual fun getPlatform(): Platform = JVMPlatform()