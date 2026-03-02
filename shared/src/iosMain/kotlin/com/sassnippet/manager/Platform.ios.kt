package com.sassnippet.manager

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val baseUrl: String get() = "http://10.0.2.2:8080"
}

actual fun getPlatform(): Platform = IOSPlatform()