package com.voidkernel.voidsu.ui.component

import androidx.compose.runtime.Composable
import com.voidkernel.voidsu.domain.model.KernelStatus

@Composable
inline fun KsuIsValid(
    status: KernelStatus,
    content: @Composable () -> Unit
) {
    if (status.isFullFeatured)
        content()
}
