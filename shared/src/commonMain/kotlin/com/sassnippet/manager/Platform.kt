package com.sassnippet.manager

interface Platform {
    val name: String
    val baseUrl: String
    val apiKey: String
}

expect fun getPlatform(): Platform