package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.update.ManagerUpdateRepository
import com.voidkernel.voidsu.domain.model.ManagerUpdateChannel
import com.voidkernel.voidsu.domain.model.ManagerUpdateInfo

class CheckManagerUpdateUseCase(
    private val repository: ManagerUpdateRepository,
) {
    suspend operator fun invoke(channel: ManagerUpdateChannel): ManagerUpdateInfo? =
        when (channel) {
            ManagerUpdateChannel.STABLE -> repository.checkStableUpdate()
            ManagerUpdateChannel.BETA -> repository.checkBetaUpdate()
        }
}
