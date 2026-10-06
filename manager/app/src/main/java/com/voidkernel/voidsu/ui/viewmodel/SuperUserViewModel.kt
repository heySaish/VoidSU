package com.voidkernel.voidsu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voidkernel.voidsu.R
import com.voidkernel.voidsu.domain.model.AllowlistOperationResult
import com.voidkernel.voidsu.domain.model.InstalledAppGroup
import com.voidkernel.voidsu.domain.usecase.BackupAllowlistUseCase
import com.voidkernel.voidsu.domain.usecase.GetAppProfileUseCase
import com.voidkernel.voidsu.domain.usecase.GetBooleanPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.GetManagerRuntimeInfoUseCase
import com.voidkernel.voidsu.domain.usecase.GetStringPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.ImportAllowlistUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveSuperUserStateUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshSuperUsersUseCase
import com.voidkernel.voidsu.domain.usecase.SetAppProfileUseCase
import com.voidkernel.voidsu.domain.usecase.SetBooleanPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.SetStringPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.TransliterateTextUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortType(val displayNameRes: Int, val persistKey: String) {
    NAME(R.string.sort_name, "NAME"),
    INSTALL_TIME(R.string.sort_install_time, "INSTALL_TIME"),
    UPDATE_TIME(R.string.sort_update_time, "UPDATE_TIME"),
    SIZE(R.string.sort_size, "SIZE"),
    USAGE_FREQ(R.string.sort_usage_freq, "USAGE_FREQ");

    companion object {
        fun fromPersistKey(key: String): SortType = entries.find { it.persistKey == key } ?: NAME
    }
}

data class SuperUserUiState(
    val appGroupList: List<InstalledAppGroup> = emptyList(),
    val search: String = "",
    val showSystemApps: Boolean = false,
    val currentSortType: SortType = SortType.NAME,
    val reverseOrder: Boolean = false,
    val managerUids: Set<Int> = emptySet(),
    val isRefreshing: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedUids: Set<Int> = emptySet(),
)

sealed interface SuperUserUiAction {
    data object Refresh : SuperUserUiAction
    data class BackupAllowlist(val uri: String) : SuperUserUiAction
    data class RestoreAllowlist(val uri: String) : SuperUserUiAction
    data class Search(val query: String) : SuperUserUiAction
    data class SetShowSystemApps(val enabled: Boolean) : SuperUserUiAction
    data class SetSort(val sortType: SortType) : SuperUserUiAction
    data class SetReverseOrder(val enabled: Boolean) : SuperUserUiAction
    data object StatusChanged : SuperUserUiAction
    data class SetSelectionMode(val enabled: Boolean) : SuperUserUiAction
    data class ToggleSelectApp(val uid: Int) : SuperUserUiAction
    data object SelectAll : SuperUserUiAction
    data object DeselectAll : SuperUserUiAction
    data class BatchSetRoot(val allowSu: Boolean) : SuperUserUiAction
    data class BatchSetUmount(val umount: Boolean) : SuperUserUiAction
}

sealed interface SuperUserUiEvent {
    data class Error(val message: String) : SuperUserUiEvent
    data class AllowlistOperationFinished(
        val result: AllowlistOperationResult,
        val restore: Boolean,
    ) : SuperUserUiEvent
}

private data class SuperUserControls(
    val search: String = "",
    val showSystemApps: Boolean = false,
    val sortType: SortType = SortType.NAME,
    val reverseOrder: Boolean = false,
)

private data class SelectionState(
    val isSelectionMode: Boolean = false,
    val selectedUids: Set<Int> = emptySet(),
)

class SuperUserViewModel(
    observeSuperUserState: ObserveSuperUserStateUseCase,
    private val refreshSuperUsers: RefreshSuperUsersUseCase,
    private val backupAllowlistUseCase: BackupAllowlistUseCase,
    private val importAllowlistUseCase: ImportAllowlistUseCase,
    getBooleanPreference: GetBooleanPreferenceUseCase,
    getStringPreference: GetStringPreferenceUseCase,
    private val setBooleanPreference: SetBooleanPreferenceUseCase,
    private val setStringPreference: SetStringPreferenceUseCase,
    private val transliterateText: TransliterateTextUseCase,
    private val getManagerRuntimeInfo: GetManagerRuntimeInfoUseCase,
    private val getAppProfileUseCase: GetAppProfileUseCase,
    private val setAppProfileUseCase: SetAppProfileUseCase,
) : ViewModel() {
    private val sourceState = observeSuperUserState()
    private val controls = MutableStateFlow(
        SuperUserControls(
            showSystemApps = getBooleanPreference(KEY_SHOW_SYSTEM_APPS, false),
            sortType = SortType.fromPersistKey(
                getStringPreference(KEY_CURRENT_SORT_TYPE, SortType.NAME.persistKey)
                    ?: SortType.NAME.persistKey
            ),
            reverseOrder = getBooleanPreference(KEY_REVERSE_ORDER, false),
        )
    )
    private val selectionState = MutableStateFlow(SelectionState())
    private val mutableEvents = MutableSharedFlow<SuperUserUiEvent>(extraBufferCapacity = 1)
    private var refreshJob: Job? = null
    val events: SharedFlow<SuperUserUiEvent> = mutableEvents.asSharedFlow()

    private val managerUids = MutableStateFlow<Set<Int>>(emptySet())

    init {
        viewModelScope.launch {
            val info = runCatching { getManagerRuntimeInfo() }.getOrNull()
            managerUids.value = info?.managers?.map { it.uid }?.toSet().orEmpty()
        }
    }

    val state: StateFlow<SuperUserUiState> = combine(
        sourceState, controls, managerUids, selectionState,
    ) { source, local, uids, selection ->
        SuperUserUiState(
            appGroupList = buildAppGroupList(
                groups = source.groups,
                search = local.search,
                showSystemApps = local.showSystemApps,
                currentSortType = local.sortType,
                reverseOrder = local.reverseOrder,
            ),
            search = local.search,
            showSystemApps = local.showSystemApps,
            currentSortType = local.sortType,
            reverseOrder = local.reverseOrder,
            managerUids = uids,
            isRefreshing = source.refreshing,
            isSelectionMode = selection.isSelectionMode,
            selectedUids = selection.selectedUids,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SuperUserUiState())
    val uiState: StateFlow<SuperUserUiState> = state

    fun dispatch(action: SuperUserUiAction) {
        when (action) {
            SuperUserUiAction.Refresh -> refresh()
            is SuperUserUiAction.BackupAllowlist -> viewModelScope.launch {
                mutableEvents.emit(
                    SuperUserUiEvent.AllowlistOperationFinished(
                        result = backupAllowlistUseCase(action.uri),
                        restore = false,
                    )
                )
            }

            is SuperUserUiAction.RestoreAllowlist -> viewModelScope.launch {
                val result = importAllowlistUseCase(action.uri)
                if (result == AllowlistOperationResult.Success) {
                    notifySuperuserStatusChanged()
                }
                mutableEvents.emit(
                    SuperUserUiEvent.AllowlistOperationFinished(
                        result = result,
                        restore = true,
                    )
                )
            }

            is SuperUserUiAction.Search -> controls.value =
                controls.value.copy(search = action.query)

            is SuperUserUiAction.SetShowSystemApps -> {
                setBooleanPreference(KEY_SHOW_SYSTEM_APPS, action.enabled)
                controls.value = controls.value.copy(showSystemApps = action.enabled)
            }

            is SuperUserUiAction.SetSort -> {
                setStringPreference(KEY_CURRENT_SORT_TYPE, action.sortType.persistKey)
                controls.value = controls.value.copy(sortType = action.sortType)
            }

            is SuperUserUiAction.SetReverseOrder -> {
                setBooleanPreference(KEY_REVERSE_ORDER, action.enabled)
                controls.value = controls.value.copy(reverseOrder = action.enabled)
            }

            SuperUserUiAction.StatusChanged -> notifySuperuserStatusChanged()

            is SuperUserUiAction.SetSelectionMode -> {
                selectionState.value = SelectionState(
                    isSelectionMode = action.enabled,
                    selectedUids = if (action.enabled) selectionState.value.selectedUids else emptySet()
                )
            }

            is SuperUserUiAction.ToggleSelectApp -> {
                val current = selectionState.value.selectedUids
                val newSet = if (action.uid in current) current - action.uid else current + action.uid
                selectionState.value = SelectionState(
                    isSelectionMode = newSet.isNotEmpty(),
                    selectedUids = newSet
                )
            }

            SuperUserUiAction.SelectAll -> {
                val allUids = state.value.appGroupList.map { it.uid }.toSet()
                selectionState.value = SelectionState(
                    isSelectionMode = true,
                    selectedUids = allUids
                )
            }

            SuperUserUiAction.DeselectAll -> {
                selectionState.value = selectionState.value.copy(selectedUids = emptySet())
            }

            is SuperUserUiAction.BatchSetRoot -> viewModelScope.launch {
                val selected = selectionState.value.selectedUids
                val groups = state.value.appGroupList.filter { it.uid in selected }
                for (group in groups) {
                    val profile = getAppProfileUseCase(group.primaryPackageName, group.uid)
                    setAppProfileUseCase(profile.copy(allowSu = action.allowSu))
                }
                selectionState.value = SelectionState(isSelectionMode = false, selectedUids = emptySet())
                notifySuperuserStatusChanged()
            }

            is SuperUserUiAction.BatchSetUmount -> viewModelScope.launch {
                val selected = selectionState.value.selectedUids
                val groups = state.value.appGroupList.filter { it.uid in selected }
                for (group in groups) {
                    val profile = getAppProfileUseCase(group.primaryPackageName, group.uid)
                    setAppProfileUseCase(profile.copy(allowSu = false, umountModules = action.umount))
                }
                selectionState.value = SelectionState(isSelectionMode = false, selectedUids = emptySet())
                notifySuperuserStatusChanged()
            }
        }
    }
        }
    }

    suspend fun fetchAppList() {
        refreshSuperUsers()
            .onFailure { mutableEvents.tryEmit(SuperUserUiEvent.Error(it.message.orEmpty())) }
    }

    private fun notifySuperuserStatusChanged() {
        viewModelScope.launch {
            sourceState.first { !it.refreshing }
            refresh()
        }
    }

    private fun refresh() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch { fetchAppList() }
    }

    private fun buildAppGroupList(
        groups: List<InstalledAppGroup>,
        search: String,
        showSystemApps: Boolean,
        currentSortType: SortType,
        reverseOrder: Boolean,
    ): List<InstalledAppGroup> = groups
        .filter { group ->
            group.apps.any { app ->
                app.label.contains(search, true) ||
                        app.displayIdentifier.contains(search, true) ||
                        transliterateText(app.label).contains(search, true)
            }
        }
        .filter { group ->
            group.isWebViewZygote || group.uid == 2000 || showSystemApps || group.apps.any { !it.isSystem }
        }
        .sortedWith { first, second ->
            val priority = groupPriority(first).compareTo(groupPriority(second))
            if (priority != 0) {
                priority
            } else {
                val base = when (currentSortType) {
                    SortType.NAME ->
                        first.mainApp.label.compareTo(second.mainApp.label, true)

                    SortType.INSTALL_TIME ->
                        first.mainApp.firstInstallTime.compareTo(second.mainApp.firstInstallTime)

                    SortType.UPDATE_TIME ->
                        first.mainApp.lastUpdateTime.compareTo(second.mainApp.lastUpdateTime)

                    SortType.SIZE ->
                        first.mainApp.label.compareTo(second.mainApp.label, true)

                    SortType.USAGE_FREQ ->
                        first.mainApp.label.compareTo(second.mainApp.label, true)
                }
                if (reverseOrder) -base else base
            }
        }

    private fun groupPriority(group: InstalledAppGroup): Int = when {
        group.allowSu -> 0
        group.isRecentlyInstalled -> 1
        group.hasCustomProfile -> 2
        else -> 3
    }

    private companion object {
        const val KEY_SHOW_SYSTEM_APPS = "show_system_apps"
        const val KEY_CURRENT_SORT_TYPE = "current_sort_type"
        const val KEY_REVERSE_ORDER = "reverse_order"
    }
}
