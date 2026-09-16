// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 ReSukiSU contributors
package com.voidkernel.voidsu.ui.animation.predictiveback

import com.voidkernel.voidsu.ui.viewmodel.PredictiveBackAnimation
import com.voidkernel.voidsu.ui.viewmodel.PredictiveBackExitDirection
import top.yukonga.miuix.kmp.nav.transition.NavTransition
import top.yukonga.miuix.kmp.nav.transition.NavTransitions

fun installerNavTransition(
    animation: PredictiveBackAnimation,
    exitDirection: PredictiveBackExitDirection,
): NavTransition = when (animation) {
    PredictiveBackAnimation.None -> NoPredictiveBackTransition
    PredictiveBackAnimation.MIUIX -> NavTransitions.MiuixDefault
    PredictiveBackAnimation.AOSP -> AospNavTransition
    PredictiveBackAnimation.Scale -> scaleNavTransition(exitDirection)
    PredictiveBackAnimation.KernelSUClassic -> ClassicNavTransition
}
