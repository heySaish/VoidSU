package com.voidkernel.voidsu

import android.system.Os

/**
 * @author weishu
 * @date 2022/12/10.
 */

data class KernelVersion(val major: Int, val patchLevel: Int, val subLevel: Int) {
    override fun toString(): String {
        return "$major.$patchLevel.$subLevel"
    }

    fun isGKI(): Boolean {

        // kernel 6.x
        if (major > 5) {
            return true
        }

        // kernel 5.10.x
        if (major == 5) {
            return patchLevel >= 10
        }

        return false
    }

    fun isULegacy(): Boolean {
        return major == 3
    }

    fun isLegacy(): Boolean {
        return major == 4 && patchLevel in 1..18
    }

    fun isGKI1(): Boolean {
        return (major == 4 && patchLevel >= 19) || (major == 5 && patchLevel < 10)
    }

}

fun KernelVersion.getKernelType(): String {
    return when {
        isGKI() -> "GKI2"
        isULegacy() -> "U-LEGACY"
        isLegacy() -> "LEGACY"
        isGKI1() -> "GKI1"
        else -> "UNKNOWN"
    }
}

fun KernelVersion.isKernel510(): Boolean {
    return major == 5 && patchLevel == 10
}

fun parseKernelVersion(version: String): KernelVersion {
    val find = "(\\d+)\\.(\\d+)\\.(\\d+)".toRegex().find(version)
    return if (find != null) {
        KernelVersion(find.groupValues[1].toInt(), find.groupValues[2].toInt(), find.groupValues[3].toInt())
    } else {
        KernelVersion(-1, -1, -1)
    }
}

fun getKernelVersion(): KernelVersion {
    Os.uname().release.let {
        return parseKernelVersion(it)
    }
}

fun getVoidKernelVersion(): String? {
    return runCatching {
        val sysfsVersion = java.io.File("/sys/kernel/void_kernel/version")
        if (sysfsVersion.exists()) {
            sysfsVersion.readText().trim()
        } else {
            val sysfsAlt = java.io.File("/sys/kernel/voidsu/version")
            if (sysfsAlt.exists()) sysfsAlt.readText().trim() else null
        }
    }.getOrNull()
}

fun isVoidKernel(): Boolean {
    val sysfsVersion = getVoidKernelVersion()
    if (!sysfsVersion.isNullOrEmpty()) return true
    val unameRelease = runCatching { Os.uname().release }.getOrDefault("")
    return unameRelease.contains("voidkernel", ignoreCase = true)
}