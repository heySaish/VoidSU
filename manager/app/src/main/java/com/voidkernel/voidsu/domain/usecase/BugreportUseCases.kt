package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.logging.BugreportRepository
import java.io.File

class GenerateBugreportUseCase(
    private val repository: BugreportRepository,
) {
    operator fun invoke(): File = repository.create()
}
