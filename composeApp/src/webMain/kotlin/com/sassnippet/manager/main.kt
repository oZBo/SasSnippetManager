package com.sassnippet.manager

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.sassnippet.manager.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin {}
    ComposeViewport {
        App()
    }
}
