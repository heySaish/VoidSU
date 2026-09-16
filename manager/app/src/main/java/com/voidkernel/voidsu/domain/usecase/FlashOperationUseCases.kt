package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.flash.FlashRepository
import com.voidkernel.voidsu.domain.model.FlashOperation

class ExecuteFlashOperationUseCase(private val repository: FlashRepository) {
    operator fun invoke(operation: FlashOperation) = repository.execute(operation)
}
