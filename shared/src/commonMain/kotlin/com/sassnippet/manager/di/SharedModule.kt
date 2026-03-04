package com.sassnippet.manager.di

import com.sassnippet.manager.getPlatform
import com.sassnippet.manager.network.SnippetApiClient
import com.sassnippet.manager.repository.SnippetRepository
import org.koin.dsl.module

val networkModule = module {
    single { SnippetApiClient(getPlatform().baseUrl) }
}

val repositoryModule = module {
    single { SnippetRepository(get()) }
}

val sharedModules = listOf(networkModule, repositoryModule)