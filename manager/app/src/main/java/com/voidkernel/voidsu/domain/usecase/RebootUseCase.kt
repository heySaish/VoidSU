package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.application.ApplicationControlRepository

class RebootUseCase(
    private val repository: ApplicationControlRepository,
) {
    suspend operator fun invoke(reason: String = "") = repository.reboot(reason)
}
