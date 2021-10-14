package de.coldtea.moin.di

import de.coldtea.moin.MainViewModel
import de.coldtea.moin.ui.alarm.lockscreen.LockScreenAlarmViewModel
import de.coldtea.moin.ui.debugview.DebugViewModel
import de.coldtea.moin.ui.dialogfragments.label.AlarmLabelDialogViewModel
import de.coldtea.moin.ui.playlist.PlaylistViewModel
import de.coldtea.moin.ui.searchspotify.SearchSpotifyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModel for Detail View
    viewModel { DebugViewModel(get(), get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { SearchSpotifyViewModel(get(), get(), get(), get()) }
    viewModel { PlaylistViewModel(get()) }
    viewModel { LockScreenAlarmViewModel(get()) }
    viewModel { AlarmLabelDialogViewModel(get()) }
}