package com.voidkernel.voidsu.di

import coil.ImageLoader
import com.voidkernel.voidsu.BuildConfig
import com.voidkernel.voidsu.data.AppSettingsRepository
import com.voidkernel.voidsu.data.application.ApplicationControlRepository
import com.voidkernel.voidsu.data.application.DynamicManagerRepository
import com.voidkernel.voidsu.data.download.DownloadRepository
import com.voidkernel.voidsu.data.file.ModuleFileRepository
import com.voidkernel.voidsu.data.flash.FlashRepository
import com.voidkernel.voidsu.data.kernel.KernelRepository
import com.voidkernel.voidsu.data.kernel.UmountRepository
import com.voidkernel.voidsu.data.logging.BugreportRepository
import com.voidkernel.voidsu.data.logging.SulogRepository
import com.voidkernel.voidsu.data.module.ModuleActionRepository
import com.voidkernel.voidsu.data.module.ModuleCatalogRepository
import com.voidkernel.voidsu.data.module.ModulePreferencesRepository
import com.voidkernel.voidsu.data.module.ModuleRepository
import com.voidkernel.voidsu.data.network.NetworkRequestRepository
import com.voidkernel.voidsu.data.network.NetworkStatusRepository
import com.voidkernel.voidsu.data.network.WebResourceRepository
import com.voidkernel.voidsu.data.packageinfo.AppIconDataSource
import com.voidkernel.voidsu.data.packageinfo.InstalledPackageCache
import com.voidkernel.voidsu.data.packageinfo.InstalledPackageRepository
import com.voidkernel.voidsu.data.packageinfo.RootServiceRepository
import com.voidkernel.voidsu.data.packageinfo.SuperUserRepository
import com.voidkernel.voidsu.data.profile.ProfileRepository
import com.voidkernel.voidsu.data.profile.ProfileTemplateRepository
import com.voidkernel.voidsu.data.settings.LocaleHelper
import com.voidkernel.voidsu.data.settings.LocaleRepository
import com.voidkernel.voidsu.data.settings.SettingsPlatformRepository
import com.voidkernel.voidsu.data.shell.KsuCliRepository
import com.voidkernel.voidsu.data.shell.ShortcutRepository
import com.voidkernel.voidsu.data.startup.ApplicationInitializationRepository
import com.voidkernel.voidsu.data.startup.StartupRepository
import com.voidkernel.voidsu.data.susfs.SuSFSConfigHelper
import com.voidkernel.voidsu.data.susfs.SuSFSRepository
import com.voidkernel.voidsu.data.system.HomeRuntimeRepository
import com.voidkernel.voidsu.data.system.HomeStateRepository
import com.voidkernel.voidsu.data.text.HanziToPinyin
import com.voidkernel.voidsu.data.theme.MonetCompatColorSource
import com.voidkernel.voidsu.data.theme.ThemeRepository
import com.voidkernel.voidsu.data.update.ManagerUpdateRepository
import com.voidkernel.voidsu.data.webui.WebUiRepository
import com.voidkernel.voidsu.domain.text.TextTransliterator
import com.voidkernel.voidsu.domain.usecase.AddUmountPathUseCase
import com.voidkernel.voidsu.domain.usecase.ApplyLanguageUseCase
import com.voidkernel.voidsu.domain.usecase.BackupAllowlistUseCase
import com.voidkernel.voidsu.domain.usecase.CalculateInstalledModuleSizeUseCase
import com.voidkernel.voidsu.domain.usecase.CheckFlashModuleMountUseCase
import com.voidkernel.voidsu.domain.usecase.CheckManagerUpdateUseCase
import com.voidkernel.voidsu.domain.usecase.CleanSulogUseCase
import com.voidkernel.voidsu.domain.usecase.ClearDynamicManagerUseCase
import com.voidkernel.voidsu.domain.usecase.ConfigureSuLogUseCase
import com.voidkernel.voidsu.domain.usecase.ControlAppUseCase
import com.voidkernel.voidsu.domain.usecase.DeleteProfileTemplateUseCase
import com.voidkernel.voidsu.domain.usecase.EnableSulogUseCase
import com.voidkernel.voidsu.domain.usecase.EnqueueDownloadUseCase
import com.voidkernel.voidsu.domain.usecase.EnqueueManagerUpdateUseCase
import com.voidkernel.voidsu.domain.usecase.EnsureManagerInstalledUseCase
import com.voidkernel.voidsu.domain.usecase.ExecuteFlashOperationUseCase
import com.voidkernel.voidsu.domain.usecase.ExecuteModuleActionUseCase
import com.voidkernel.voidsu.domain.usecase.ExportProfileTemplatesUseCase
import com.voidkernel.voidsu.domain.usecase.ExtractModuleIdUseCase
import com.voidkernel.voidsu.domain.usecase.ExtractModuleNameUseCase
import com.voidkernel.voidsu.domain.usecase.FetchRemoteTextUseCase
import com.voidkernel.voidsu.domain.usecase.GenerateBugreportUseCase
import com.voidkernel.voidsu.domain.usecase.GetAppProfileUseCase
import com.voidkernel.voidsu.domain.usecase.GetAppSepolicyUseCase
import com.voidkernel.voidsu.domain.usecase.GetBooleanPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.GetCatalogModuleUseCase
import com.voidkernel.voidsu.domain.usecase.GetDefaultUmountModulesUseCase
import com.voidkernel.voidsu.domain.usecase.GetHomeBasicInfoUseCase
import com.voidkernel.voidsu.domain.usecase.GetInstallEnvironmentUseCase
import com.voidkernel.voidsu.domain.usecase.GetKernelFeatureSettingsUseCase
import com.voidkernel.voidsu.domain.usecase.GetKernelStatusUseCase
import com.voidkernel.voidsu.domain.usecase.GetManagerRuntimeInfoUseCase
import com.voidkernel.voidsu.domain.usecase.GetPlatformFeatureStatusUseCase
import com.voidkernel.voidsu.domain.usecase.GetProfileTemplateUseCase
import com.voidkernel.voidsu.domain.usecase.GetStringPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.GetStringSetPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.GetSuSFSStatusUseCase
import com.voidkernel.voidsu.domain.usecase.GetSuperUserAppGroupUseCase
import com.voidkernel.voidsu.domain.usecase.ImportAllowlistUseCase
import com.voidkernel.voidsu.domain.usecase.ImportProfileTemplatesUseCase
import com.voidkernel.voidsu.domain.usecase.InitializeApplicationUseCase
import com.voidkernel.voidsu.domain.usecase.IsLateLoadModeUseCase
import com.voidkernel.voidsu.domain.usecase.IsModuleUriAccessibleUseCase
import com.voidkernel.voidsu.domain.usecase.IsNetworkAvailableUseCase
import com.voidkernel.voidsu.domain.usecase.IsSystemLanguageSettingsUseCase
import com.voidkernel.voidsu.domain.usecase.LaunchSystemLanguageSettingsUseCase
import com.voidkernel.voidsu.domain.usecase.LoadSettingsPlatformUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveCatalogModulesUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveDownloadUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveDynamicManagerStateUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveInstalledModulesUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveKernelFlashUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveModuleCatalogOfflineUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveModuleCatalogRefreshingUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveProfileTemplateOfflineUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveProfileTemplateRefreshingUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveProfileTemplatesUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveStartupStateUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveSulogStateUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveSuperUserStateUseCase
import com.voidkernel.voidsu.domain.usecase.ObserveUmountStateUseCase
import com.voidkernel.voidsu.domain.usecase.RebootUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshDynamicManagerUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshInstalledModulesUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshModuleCatalogUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshProfileTemplatesUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshSulogUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshSuperUsersUseCase
import com.voidkernel.voidsu.domain.usecase.RefreshUmountPathsUseCase
import com.voidkernel.voidsu.domain.usecase.RemovePreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.RemoveUmountPathUseCase
import com.voidkernel.voidsu.domain.usecase.SaveModuleActionLogUseCase
import com.voidkernel.voidsu.domain.usecase.SaveProfileTemplateUseCase
import com.voidkernel.voidsu.domain.usecase.SelectDynamicManagerUseCase
import com.voidkernel.voidsu.domain.usecase.SetAppProfileUseCase
import com.voidkernel.voidsu.domain.usecase.SetAppSepolicyUseCase
import com.voidkernel.voidsu.domain.usecase.SetBooleanPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.SetDefaultUmountModulesUseCase
import com.voidkernel.voidsu.domain.usecase.SetKernelUmountEnabledUseCase
import com.voidkernel.voidsu.domain.usecase.SetManualDynamicManagerUseCase
import com.voidkernel.voidsu.domain.usecase.SetModuleEnabledUseCase
import com.voidkernel.voidsu.domain.usecase.SetModuleRemovedUseCase
import com.voidkernel.voidsu.domain.usecase.SetSelinuxHideEnabledUseCase
import com.voidkernel.voidsu.domain.usecase.SetStringPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.SetStringSetPreferenceUseCase
import com.voidkernel.voidsu.domain.usecase.SetSuEnabledUseCase
import com.voidkernel.voidsu.domain.usecase.StartKernelFlashUseCase
import com.voidkernel.voidsu.domain.usecase.SuSFSConfigUseCase
import com.voidkernel.voidsu.domain.usecase.TakeModuleUriPermissionUseCase
import com.voidkernel.voidsu.domain.usecase.TransliterateTextUseCase
import com.voidkernel.voidsu.domain.usecase.UpdateAppearanceUseCase
import com.voidkernel.voidsu.domain.usecase.UpdateCachedModuleEnabledUseCase
import com.voidkernel.voidsu.domain.usecase.UpdatePlatformSettingUseCase
import com.voidkernel.voidsu.domain.usecase.ValidateSepolicyUseCase
import com.voidkernel.voidsu.ui.activity.util.ThemeUtils
import com.voidkernel.voidsu.ui.component.ZipFileDetector
import com.voidkernel.voidsu.ui.theme.BackgroundManager
import com.voidkernel.voidsu.ui.theme.CardConfig
import com.voidkernel.voidsu.ui.theme.ThemeConfig
import com.voidkernel.voidsu.ui.util.module.Shortcut
import com.voidkernel.voidsu.ui.viewmodel.AppProfileViewModel
import com.voidkernel.voidsu.ui.viewmodel.DynamicManagerViewModel
import com.voidkernel.voidsu.ui.viewmodel.ExecuteModuleActionViewModel
import com.voidkernel.voidsu.ui.viewmodel.FlashViewModel
import com.voidkernel.voidsu.ui.viewmodel.HomeViewModel
import com.voidkernel.voidsu.ui.viewmodel.InstallViewModel
import com.voidkernel.voidsu.ui.viewmodel.KernelFlashViewModel
import com.voidkernel.voidsu.ui.viewmodel.MainIntentViewModel
import com.voidkernel.voidsu.ui.viewmodel.ModuleDetailViewModel
import com.voidkernel.voidsu.ui.viewmodel.ModuleRepoViewModel
import com.voidkernel.voidsu.ui.viewmodel.ModuleViewModel
import com.voidkernel.voidsu.ui.viewmodel.SettingsViewModel
import com.voidkernel.voidsu.ui.viewmodel.SuSFSViewModel
import com.voidkernel.voidsu.ui.viewmodel.SulogViewModel
import com.voidkernel.voidsu.ui.viewmodel.SuperUserViewModel
import com.voidkernel.voidsu.ui.viewmodel.TemplateEditorViewModel
import com.voidkernel.voidsu.ui.viewmodel.TemplateViewModel
import com.voidkernel.voidsu.ui.viewmodel.UmountManagerScreenViewModel
import com.voidkernel.voidsu.ui.webui.MonetColorsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

val applicationScopeQualifier = named("applicationScope")

val coreModule = module {
    single<CoroutineScope>(applicationScopeQualifier) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    single {
        OkHttpClient.Builder()
            .cache(Cache(File(androidApplication().cacheDir, "okhttp"), 10L * 1024L * 1024L))
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "ReSukiSU/${BuildConfig.VERSION_CODE}")
                        .header("Accept-Language", Locale.getDefault().toLanguageTag())
                        .build()
                )
            }
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }
    single {
        val application = androidApplication()
        val iconSize = application.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
        ImageLoader.Builder(application)
            .components {
                add(AppIconKeyer())
                add(AppIconFetcher.Factory(iconSize, false, application))
            }
            .build()
    }
}

val repositoryModule = module {
    single { KsuCliRepository(androidApplication()) }
    singleOf(::InstalledPackageCache)
    singleOf(::AppIconDataSource)
    singleOf(::RootServiceRepository)
    singleOf(::InstalledPackageRepository)
    single {
        SuperUserRepository(
            application = get(),
            cache = get(),
            installedPackageRepository = get(),
            profileRepository = get(),
            applicationScope = get(applicationScopeQualifier),
        )
    }
    single {
        AppSettingsRepository(
            context = androidApplication(),
            applicationScope = get(applicationScopeQualifier),
        )
    }
    singleOf(::StartupRepository)
    single {
        ApplicationInitializationRepository(
            application = get(),
            imageLoader = get(),
            applicationScope = get(applicationScopeQualifier),
            flashRepository = get(),
            ksuCliRepository = get(),
            monetCompatColorSource = get(),
        )
    }
    singleOf(::ManagerUpdateRepository)
    singleOf(::ApplicationControlRepository)
    singleOf(::DownloadRepository)
    single { FlashRepository(get(), get(applicationScopeQualifier), get(), get()) }
    singleOf(::KernelRepository)
    singleOf(::HomeRuntimeRepository)
    singleOf(::HomeStateRepository)
    singleOf(::NetworkStatusRepository)
    singleOf(::NetworkRequestRepository)
    singleOf(::DynamicManagerRepository)
    singleOf(::SulogRepository)
    singleOf(::BugreportRepository)
    singleOf(::UmountRepository)
    singleOf(::ModuleCatalogRepository)
    singleOf(::ModuleRepository)
    singleOf(::ModulePreferencesRepository)
    singleOf(::ModuleActionRepository)
    singleOf(::WebResourceRepository)
    singleOf(::WebUiRepository)
    singleOf(::ModuleFileRepository)
    singleOf(::ProfileRepository)
    singleOf(::ProfileTemplateRepository)
    singleOf(::SuSFSConfigHelper)
    singleOf(::SuSFSRepository)
    singleOf(::MonetCompatColorSource)
    singleOf(::ThemeRepository)
    single {
        val themeRepository = get<ThemeRepository>()
        ThemeConfig(themeRepository::defaultSeedColor)
    }
    singleOf(::CardConfig)
    singleOf(::BackgroundManager)
    singleOf(::ThemeUtils)
    singleOf(::LocaleHelper)
    singleOf(::LocaleRepository)
    singleOf(::SettingsPlatformRepository)
    singleOf(::ShortcutRepository)
    singleOf(::Shortcut)
    singleOf(::MonetColorsProvider)
    singleOf(::ZipFileDetector)
    single { HanziToPinyin.create() } bind TextTransliterator::class
}

val useCaseModule = module {
    factoryOf(::InitializeApplicationUseCase)
    factoryOf(::GetHomeBasicInfoUseCase)
    factoryOf(::IsNetworkAvailableUseCase)
    factoryOf(::LoadSettingsPlatformUseCase)
    factoryOf(::UpdateAppearanceUseCase)
    factoryOf(::UpdatePlatformSettingUseCase)
    factoryOf(::GetPlatformFeatureStatusUseCase)
    factoryOf(::CheckManagerUpdateUseCase)
    factoryOf(::EnsureManagerInstalledUseCase)
    factoryOf(::RebootUseCase)
    factoryOf(::EnqueueDownloadUseCase)
    factoryOf(::EnqueueManagerUpdateUseCase)
    factoryOf(::ObserveDownloadUseCase)
    factoryOf(::GetKernelStatusUseCase)
    factoryOf(::GetInstallEnvironmentUseCase)
    factoryOf(::ExecuteFlashOperationUseCase)
    factoryOf(::CheckFlashModuleMountUseCase)
    factoryOf(::GetManagerRuntimeInfoUseCase)
    factoryOf(::GetKernelFeatureSettingsUseCase)
    factoryOf(::SetSuEnabledUseCase)
    factoryOf(::SetKernelUmountEnabledUseCase)
    factoryOf(::ConfigureSuLogUseCase)
    factoryOf(::SetSelinuxHideEnabledUseCase)
    factoryOf(::SetDefaultUmountModulesUseCase)
    factoryOf(::IsLateLoadModeUseCase)
    factoryOf(::GetAppProfileUseCase)
    factoryOf(::SetAppProfileUseCase)
    factoryOf(::GetAppSepolicyUseCase)
    factoryOf(::SetAppSepolicyUseCase)
    factoryOf(::ControlAppUseCase)
    factoryOf(::ValidateSepolicyUseCase)
    factoryOf(::GetDefaultUmountModulesUseCase)
    factoryOf(::GetSuSFSStatusUseCase)
    factoryOf(::SuSFSConfigUseCase)
    factoryOf(::ApplyLanguageUseCase)
    factoryOf(::IsSystemLanguageSettingsUseCase)
    factoryOf(::LaunchSystemLanguageSettingsUseCase)
    factoryOf(::GenerateBugreportUseCase)
    factoryOf(::ObserveStartupStateUseCase)
    factoryOf(::GetSuperUserAppGroupUseCase)
    factoryOf(::ObserveCatalogModulesUseCase)
    factoryOf(::ObserveModuleCatalogRefreshingUseCase)
    factoryOf(::ObserveModuleCatalogOfflineUseCase)
    factoryOf(::RefreshModuleCatalogUseCase)
    factoryOf(::GetCatalogModuleUseCase)
    factoryOf(::ObserveProfileTemplatesUseCase)
    factoryOf(::ObserveProfileTemplateRefreshingUseCase)
    factoryOf(::ObserveProfileTemplateOfflineUseCase)
    factoryOf(::RefreshProfileTemplatesUseCase)
    factoryOf(::GetProfileTemplateUseCase)
    factoryOf(::SaveProfileTemplateUseCase)
    factoryOf(::DeleteProfileTemplateUseCase)
    factoryOf(::ImportProfileTemplatesUseCase)
    factoryOf(::ExportProfileTemplatesUseCase)
    factoryOf(::GetBooleanPreferenceUseCase)
    factoryOf(::SetBooleanPreferenceUseCase)
    factoryOf(::GetStringPreferenceUseCase)
    factoryOf(::SetStringPreferenceUseCase)
    factoryOf(::GetStringSetPreferenceUseCase)
    factoryOf(::SetStringSetPreferenceUseCase)
    factoryOf(::ObserveDynamicManagerStateUseCase)
    factoryOf(::RefreshDynamicManagerUseCase)
    factoryOf(::SelectDynamicManagerUseCase)
    factoryOf(::SetManualDynamicManagerUseCase)
    factoryOf(::ClearDynamicManagerUseCase)
    factoryOf(::ObserveSulogStateUseCase)
    factoryOf(::RefreshSulogUseCase)
    factoryOf(::EnableSulogUseCase)
    factoryOf(::CleanSulogUseCase)
    factoryOf(::ObserveUmountStateUseCase)
    factoryOf(::RefreshUmountPathsUseCase)
    factoryOf(::AddUmountPathUseCase)
    factoryOf(::RemoveUmountPathUseCase)
    factoryOf(::ObserveKernelFlashUseCase)
    factoryOf(::StartKernelFlashUseCase)
    factoryOf(::RemovePreferenceUseCase)
    factoryOf(::ObserveSuperUserStateUseCase)
    factoryOf(::RefreshSuperUsersUseCase)
    factoryOf(::BackupAllowlistUseCase)
    factoryOf(::ImportAllowlistUseCase)
    factoryOf(::FetchRemoteTextUseCase)
    factoryOf(::IsModuleUriAccessibleUseCase)
    factoryOf(::TakeModuleUriPermissionUseCase)
    factoryOf(::ExtractModuleNameUseCase)
    factoryOf(::ExtractModuleIdUseCase)
    factoryOf(::ObserveInstalledModulesUseCase)
    factoryOf(::RefreshInstalledModulesUseCase)
    factoryOf(::CalculateInstalledModuleSizeUseCase)
    factoryOf(::UpdateCachedModuleEnabledUseCase)
    factoryOf(::ExecuteModuleActionUseCase)
    factoryOf(::SaveModuleActionLogUseCase)
    factoryOf(::SetModuleEnabledUseCase)
    factoryOf(::SetModuleRemovedUseCase)
    factoryOf(::TransliterateTextUseCase)
}

val viewModelModule = module {
    viewModel { parameters ->
        AppProfileViewModel(
            uid = parameters[0],
            packageName = parameters[1],
            getAppGroup = get(),
            getProfile = get(),
            getDefaultUmountModules = get(),
            setProfile = get(),
            getSepolicy = get(),
            setSepolicy = get(),
            controlApp = get(),
            validateSepolicy = get(),
        )
    }
    viewModelOf(::HomeViewModel)
    viewModelOf(::InstallViewModel)
    viewModelOf(::MainIntentViewModel)
    viewModelOf(::KernelFlashViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ModuleViewModel)
    viewModelOf(::SuperUserViewModel)
    viewModelOf(::SuSFSViewModel)
    viewModelOf(::ModuleRepoViewModel)
    viewModel { parameters -> ModuleDetailViewModel(parameters[0], get()) }
    viewModelOf(::TemplateViewModel)
    viewModel { parameters ->
        TemplateEditorViewModel(
            templateId = parameters[0],
            readOnly = parameters[1],
            isCreation = parameters[2],
            getTemplate = get(),
            saveTemplate = get(),
            deleteTemplate = get(),
        )
    }
    viewModelOf(::SulogViewModel)
    viewModelOf(::DynamicManagerViewModel)
    viewModelOf(::FlashViewModel)
    viewModelOf(::UmountManagerScreenViewModel)
    viewModel { parameters ->
        ExecuteModuleActionViewModel(
            moduleId = parameters[0],
            executeModuleAction = get(),
            saveModuleActionLog = get(),
        )
    }
}

val appModules = listOf(coreModule, repositoryModule, useCaseModule, viewModelModule)
