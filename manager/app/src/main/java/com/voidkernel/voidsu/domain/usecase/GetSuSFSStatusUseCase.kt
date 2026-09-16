package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.data.susfs.SuSFSRepository

class GetSuSFSStatusUseCase(private val repository: SuSFSRepository) {
    suspend operator fun invoke() = repository.getStatus()
}

