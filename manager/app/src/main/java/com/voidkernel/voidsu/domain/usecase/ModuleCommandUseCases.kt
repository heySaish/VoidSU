package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.module.ModuleRepository

class SetModuleEnabledUseCase(
    private val repository: ModuleRepository,
) {
    suspend operator fun invoke(moduleId: String, enabled: Boolean) =
        repository.setModuleEnabled(moduleId, enabled)
}

class SetModuleRemovedUseCase(
    private val repository: ModuleRepository,
) {
    suspend operator fun invoke(moduleId: String, removed: Boolean) =
        repository.setModuleRemoved(moduleId, removed)
}
