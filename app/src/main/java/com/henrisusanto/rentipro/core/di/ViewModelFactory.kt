package com.henrisusanto.rentipro.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Generic ViewModel factory for manual DI.
 * Usage: ViewModelProvider(owner, ViewModelFactory(container) { MyViewModel(container.xxx) })
 */
class ViewModelFactory(
    private val container: AppContainer,
    private val creator: (AppContainer) -> ViewModel,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator(container) as T
    }
}

inline fun <reified VM : ViewModel> viewModelFactory(
    container: AppContainer,
    crossinline creator: (AppContainer) -> VM,
): ViewModelFactory = ViewModelFactory(container) { creator(it) as ViewModel }
