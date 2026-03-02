package com.sassnippet.manager

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val baseUrl: String get() = "http://localhost:8080"
}

actual fun getPlatform(): Platform = WasmPlatform()