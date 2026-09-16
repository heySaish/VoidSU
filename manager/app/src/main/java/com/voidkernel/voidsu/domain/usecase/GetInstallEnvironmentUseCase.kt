package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.flash.FlashRepository
import com.voidkernel.voidsu.domain.model.InstallEnvironment

class GetInstallEnvironmentUseCase(
    private val repository: FlashRepository,
) {
    fun cached(): InstallEnvironment? = repository.installEnvironment.value

    suspend operator fun invoke(forceRefresh: Boolean = false) =
        repository.getInstallEnvironment(forceRefresh)
}
