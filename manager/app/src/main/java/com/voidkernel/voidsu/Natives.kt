package com.voidkernel.voidsu

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

/**
 * Native bridge for VoidSU with ReSukiSU UI support
 */
object Natives {
    const val MINIMAL_SUPPORTED_KERNEL = 33110

    const val KERNEL_SU_DOMAIN = "u:r:ksu:s0"

    const val ROOT_UID = 0
    const val ROOT_GID = 0

    const val ALLOWLIST_RESTORE_SUCCESS = 0
    const val ALLOWLIST_RESTORE_INVALID_FILE = 1
    const val ALLOWLIST_RESTORE_UNSUPPORTED_VERSION = 2
    const val ALLOWLIST_RESTORE_IO_ERROR = 3
    const val ALLOWLIST_RESTORE_PROFILE_ERROR = 4

    init {
        System.loadLibrary("kernelsu")
    }

    val version: Int
        external get

    val allowList: IntArray
        external get

    val isSafeMode: Boolean
        external get

    val isLkmMode: Boolean
        external get

    val isLkmBundled: Boolean
        get() = false

    val isLateLoadMode: Boolean
        external get

    val isManager: Boolean
        external get

    val isPrBuild: Boolean
        get() = false

    fun getFullVersion(): String {
        return try {
            getVersionTag() ?: version.toString()
        } catch (e: Throwable) {
            version.toString()
        }
    }

    @JvmStatic
    fun createRootShellBuilder(global: Boolean = false): com.topjohnwu.superuser.Shell.Builder {
        return com.topjohnwu.superuser.Shell.Builder.create().apply {
            if (global) {
                setFlags(com.topjohnwu.superuser.Shell.FLAG_MOUNT_MASTER)
            }
        }
    }

    enum class KernelPatchImplementation {
        NONE, OFFICIAL, KPATCH_NEXT, SUKISU
    }

    fun getKernelPatchImplementation(): KernelPatchImplementation = KernelPatchImplementation.NONE

    external fun uidShouldUmount(uid: Int): Boolean

    external fun getManagerAppid(): Int

    external fun getHookMode(): String?

    fun getHookType(): String = getHookMode() ?: "Kprobes"

    external fun getVersionTag(): String?

    external fun isZygiskEnabled(): Boolean

    external fun getAppProfile(key: String?, uid: Int): Profile
    external fun setAppProfile(profile: Profile?): Boolean

    fun restoreAllowlistFromFd(fd: Int, failedUid: IntArray): Int = ALLOWLIST_RESTORE_UNSUPPORTED_VERSION

    external fun isSuEnabled(): Boolean
    external fun setSuEnabled(enabled: Boolean): Boolean

    fun isSuLogEnabled(): Boolean = false
    fun setSuLogEnabled(enabled: Boolean): Boolean = false

    external fun isKernelUmountEnabled(): Boolean
    external fun setKernelUmountEnabled(enabled: Boolean): Boolean

    fun isSelinuxHideEnabled(): Boolean = false
    fun setSelinuxHideEnabled(enabled: Boolean): Int = -1

    fun getDynamicManager(): DynamicManagerConfig? = null
    fun getManagersList(): ManagersList? = null

    external fun getUserName(uid: Int): String?

    external fun isAvcSpoofEnabled(): Boolean
    external fun setAvcSpoofEnabled(enabled: Boolean): Boolean

    external fun getSuperuserCount(): Int

    val kernelUAPIVersion: Int
        get() = 1

    val managerUAPIVersion: Int
        get() = 1

    fun isFullFeatured(): Boolean {
        return try { isManager } catch (e: Throwable) { true }
    }

    private const val NON_ROOT_DEFAULT_PROFILE_KEY = "$"
    private const val NOBODY_UID = 9999

    fun setDefaultUmountModules(umountModules: Boolean): Boolean {
        Profile(
            NON_ROOT_DEFAULT_PROFILE_KEY,
            NOBODY_UID,
            false,
            umountModules = umountModules
        ).let {
            return setAppProfile(it)
        }
    }

    fun isDefaultUmountModules(): Boolean {
        getAppProfile(NON_ROOT_DEFAULT_PROFILE_KEY, NOBODY_UID).let {
            return it.umountModules
        }
    }

    fun requireNewKernel(): Boolean {
        return version != -1 && version < MINIMAL_SUPPORTED_KERNEL
    }

    val KSU_WORK_DIR = "/data/adb/ksu/"

    @Immutable
    @Parcelize
    @Keep
    data class DynamicManagerConfig(
        val size: Int = 0,
        val hash: String = ""
    ) : Parcelable {
        fun isValid(): Boolean = size > 0 && hash.length == 64
    }

    @Immutable
    @Parcelize
    @Keep
    data class ManagersList(
        val count: Int = 0,
        val managers: List<ManagerInfo> = emptyList()
    ) : Parcelable

    @Immutable
    @Parcelize
    @Keep
    data class ManagerInfo(
        val uid: Int = 0,
        val signatureIndex: Int = 0
    ) : Parcelable

    @Immutable
    @Parcelize
    @Keep
    data class Profile(
        val name: String,
        val currentUid: Int = 0,
        val allowSu: Boolean = false,
        val rootUseDefault: Boolean = true,
        val rootTemplate: String? = null,
        val uid: Int = ROOT_UID,
        val gid: Int = ROOT_GID,
        val groups: List<Int> = mutableListOf(),
        val capabilities: List<Int> = mutableListOf(),
        val context: String = KERNEL_SU_DOMAIN,
        val namespace: Int = Namespace.INHERITED.ordinal,
        val nonRootUseDefault: Boolean = true,
        val umountModules: Boolean = true,
        var rules: String = "",
        val flags: Long = FLAG_KSU_NO_NEW_PRIVS,
    ) : Parcelable {
        @Keep
        enum class RootProfileFlag(val display: String, @param:StringRes val desc: Int) {
            NO_NEW_PRIVS(
                "NO_NEW_PRIVS",
                R.string.profile_flags_desc_no_new_privs
            )
        }

        enum class Namespace {
            INHERITED,
            GLOBAL,
            INDIVIDUAL,
        }

        constructor() : this("")
    }

    const val FLAG_KSU_NO_NEW_PRIVS = 1L
}

fun List<Natives.Profile.RootProfileFlag>.toRawFlags(): Long =
    fold(0L) { acc, flag -> acc.or(1L.shl(flag.ordinal)) }

fun Long.toRootProfileFlags(): List<Natives.Profile.RootProfileFlag> =
    Natives.Profile.RootProfileFlag.entries.filter { 1L.shl(it.ordinal).and(this) != 0L }.toList()
