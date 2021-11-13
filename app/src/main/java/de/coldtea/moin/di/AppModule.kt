package de.coldtea.moin.di

import de.coldtea.moin.MainViewModel
import de.coldtea.moin.ui.alarm.AlarmViewModel
import de.coldtea.moin.ui.debugview.DebugViewModel
import de.coldtea.moin.ui.debugview.mp3.Mp3ViewModel
import de.coldtea.moin.ui.dialogfragments.label.AlarmLabelDialogViewModel
import de.coldtea.moin.ui.lockscreen.LockScreenAlarmViewModel
import de.coldtea.moin.ui.playlist.PlaylistViewModel
import de.coldtea.moin.ui.searchspotify.SearchSpotifyViewModel
import de.coldtea.moin.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModel for Detail View
    viewModel { DebugViewModel(get(), get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { SearchSpotifyViewModel(get(), get(), get(), get()) }
    viewModel { PlaylistViewModel(get()) }
    viewModel { LockScreenAlarmViewModel(get(), get()) }
    viewModel { AlarmLabelDialogViewModel(get()) }
    viewModel { AlarmViewModel(get()) }
    viewModel { Mp3ViewModel() }
    viewModel { SettingsViewModel(get()) }
}