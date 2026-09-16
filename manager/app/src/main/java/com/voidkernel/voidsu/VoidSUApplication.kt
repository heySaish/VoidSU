package com.voidkernel.voidsu

import android.app.Application
import android.os.Build
import android.system.Os
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import coil.Coil
import coil.ImageLoader
import com.voidkernel.voidsu.di.appModules
import com.voidkernel.voidsu.domain.usecase.InitializeApplicationUseCase
import com.voidkernel.voidsu.Natives.createRootShellBuilder
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.io.File
import java.util.*

lateinit var ksuApp: VoidSUApplication

class VoidSUApplication : Application(), ViewModelStoreOwner {

    lateinit var okhttpClient: OkHttpClient
    private val appViewModelStore by lazy { ViewModelStore() }

    override fun onCreate() {
        super.onCreate()
        ksuApp = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (processName.endsWith("MagicaService")) {
                return
            }
        }

        Shell.setDefaultBuilder(createRootShellBuilder(true))
        Shell.enableVerboseLogging = BuildConfig.DEBUG

        val context = this
        val iconSize = resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
        Coil.setImageLoader(
            ImageLoader.Builder(context)
                .components {
                    add(AppIconKeyer())
                    add(AppIconFetcher.Factory(iconSize, false, context))
                }
                .build()
        )

        val webroot = File(dataDir, "webroot")
        if (!webroot.exists()) {
            webroot.mkdir()
        }

        // Provide working env for rust's temp_dir()
        Os.setenv("TMPDIR", cacheDir.absolutePath, true)

        okhttpClient =
            OkHttpClient.Builder().cache(Cache(File(cacheDir, "okhttp"), 10 * 1024 * 1024))
                .addInterceptor { block ->
                    block.proceed(
                        block.request().newBuilder()
                            .header("User-Agent", "VoidSU/${BuildConfig.VERSION_CODE}")
                            .header("Accept-Language", Locale.getDefault().toLanguageTag()).build()
                    )
                }.build()

        val koin = startKoin {
            androidLogger()
            androidContext(this@VoidSUApplication)
            modules(appModules)
        }.koin
        runBlocking(Dispatchers.IO) {
            koin.get<InitializeApplicationUseCase>()()
        }
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}

