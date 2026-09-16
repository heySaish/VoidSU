package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.startup.StartupRepository

class ObserveStartupStateUseCase(
    private val repository: StartupRepository,
) {
    operator fun invoke() = repository.state
}
