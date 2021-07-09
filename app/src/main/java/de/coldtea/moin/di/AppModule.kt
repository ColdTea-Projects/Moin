package de.coldtea.moin.di

import de.coldtea.moin.ui.debugview.DebugViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // ViewModel for Detail View
    viewModel { DebugViewModel(get(), get()) }

}