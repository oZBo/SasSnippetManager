package com.sassnippet.manager.di

import com.sassnippet.manager.ui.create.CreateSnippetViewModel
import com.sassnippet.manager.ui.detail.SnippetDetailViewModel
import com.sassnippet.manager.ui.list.SnippetListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModelOf(::SnippetListViewModel)
    viewModelOf(::CreateSnippetViewModel)

    viewModel { params ->
        SnippetDetailViewModel(
            repository = get(),
            snippetId = params.get()
        )
    }
}

val appModules = sharedModules + viewModelModule