package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.network.NetworkStatusRepository
import com.voidkernel.voidsu.data.system.HomeRuntimeRepository

class GetHomeBasicInfoUseCase(private val repository: HomeRuntimeRepository) {
    suspend operator fun invoke(
        managerUapiVersion: Int,
        includeSelinuxStatus: Boolean = true,
    ) = repository.getBasicInfo(managerUapiVersion, includeSelinuxStatus)
}

class IsNetworkAvailableUseCase(private val repository: NetworkStatusRepository) {
    operator fun invoke() = repository.isAvailable()
}
