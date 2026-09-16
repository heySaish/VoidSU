package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.flash.FlashRepository

class CheckFlashModuleMountUseCase(private val repository: FlashRepository) {
    suspend operator fun invoke(uri: String) = repository.moduleNeedsMount(uri)
}
