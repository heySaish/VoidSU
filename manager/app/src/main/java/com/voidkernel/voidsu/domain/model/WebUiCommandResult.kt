package com.voidkernel.voidsu.domain.model

data class WebUiCommandResult(
    val code: Int,
    val stdout: String,
    val stderr: String,
)
