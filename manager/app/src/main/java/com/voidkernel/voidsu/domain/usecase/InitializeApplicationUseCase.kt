package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.AppSettingsRepository
import com.voidkernel.voidsu.data.startup.ApplicationInitializationRepository
import com.voidkernel.voidsu.data.startup.StartupRepository

class InitializeApplicationUseCase(
    private val settingsRepository: AppSettingsRepository,
    private val startupRepository: StartupRepository,
    private val initializationRepository: ApplicationInitializationRepository,
) {
    suspend operator fun invoke() {
        runCatching {
            settingsRepository.preload()
            initializationRepository.initialize()
        }.onSuccess {
            startupRepository.markReady()
        }.onFailure { error ->
            startupRepository.markFailed(error)
        }
    }
}
