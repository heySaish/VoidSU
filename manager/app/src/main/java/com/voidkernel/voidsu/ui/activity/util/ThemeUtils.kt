package com.voidkernel.voidsu.ui.activity.util

import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import com.voidkernel.voidsu.data.AppSettingsRepository
import com.voidkernel.voidsu.data.theme.ThemeRepository
import com.voidkernel.voidsu.ui.MainActivity
import com.voidkernel.voidsu.ui.theme.BackgroundManager
import com.voidkernel.voidsu.ui.theme.CardConfig
import com.voidkernel.voidsu.ui.theme.ThemeConfig
import com.voidkernel.voidsu.ui.viewmodel.SettingsUiAction
import com.voidkernel.voidsu.ui.viewmodel.SettingsViewModel

class ThemeChangeContentObserver(
    handler: Handler,
    private val onThemeChanged: () -> Unit
) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onThemeChanged()
    }
}

class ThemeUtils(
    private val settings: AppSettingsRepository,
    private val themeConfig: ThemeConfig,
    private val themeRepository: ThemeRepository,
    private val cardConfig: CardConfig,
    private val backgroundManager: BackgroundManager,
) {

    fun initializeThemeSettings(activity: MainActivity, settingsViewModel: SettingsViewModel) {
        settingsViewModel.dispatch(SettingsUiAction.InitializeFirstRun)
        loadThemeSettings(activity)
        settingsViewModel.dispatch(SettingsUiAction.Initialize)
    }

    fun registerThemeChangeObserver(activity: MainActivity): ThemeChangeContentObserver {
        val contentObserver = ThemeChangeContentObserver(Handler(activity.mainLooper)) {
            activity.runOnUiThread {
                if (!themeConfig.preventBackgroundRefresh) {
                    themeConfig.backgroundImageLoaded = false
                    backgroundManager.loadCustomBackground()
                }
            }
        }

        activity.contentResolver.registerContentObserver(
            Settings.System.getUriFor("ui_night_mode"),
            false,
            contentObserver
        )

        return contentObserver
    }

    fun unregisterThemeChangeObserver(activity: MainActivity, observer: ThemeChangeContentObserver) {
        activity.contentResolver.unregisterContentObserver(observer)
    }

    fun onActivityPause() {
        cardConfig.save()
        settings.putBoolean("prevent_background_refresh", true)
        themeConfig.preventBackgroundRefresh = true
    }

    fun onActivityResume(activity: MainActivity) {
        settings.putBoolean("prevent_background_refresh", false)
        themeConfig.preventBackgroundRefresh = false
        loadThemeSettings(activity)
    }

    private fun loadThemeSettings(activity: MainActivity) {
        themeConfig.forceDarkMode = themeRepository.loadThemeMode()
        themeConfig.seedColor = themeRepository.loadSeedColor()
        themeConfig.useDynamicColor = themeRepository.loadDynamicColorState()
        themeConfig.dynamicColorSpec = themeRepository.loadDynamicColorSpec()
        themeConfig.dynamicPaletteStyle = themeRepository.loadDynamicPaletteStyle(
            themeConfig.dynamicColorSpec,
        )
        cardConfig.load()
        backgroundManager.loadCustomBackground()
    }
}
