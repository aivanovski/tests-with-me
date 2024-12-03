package com.github.aivanovski.testswithme.android.di

import android.annotation.SuppressLint
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.api.HttpRequestExecutor
import com.github.aivanovski.testswithme.android.data.db.AppDatabase
import com.github.aivanovski.testswithme.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.testswithme.android.data.db.dao.JobDao
import com.github.aivanovski.testswithme.android.data.db.dao.JobHistoryDao
import com.github.aivanovski.testswithme.android.data.db.dao.LocalStepRunDao
import com.github.aivanovski.testswithme.android.data.db.dao.ProjectEntryDao
import com.github.aivanovski.testswithme.android.data.db.dao.StepEntryDao
import com.github.aivanovski.testswithme.android.data.file.FileCache
import com.github.aivanovski.testswithme.android.data.file.FileCacheImpl
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testswithme.android.data.repository.UserRepository
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.data.settings.SettingsImpl
import com.github.aivanovski.testswithme.android.domain.VersionParser
import com.github.aivanovski.testswithme.android.domain.driverServer.GatewayReceiverInteractor
import com.github.aivanovski.testswithme.android.domain.driverServer.GatewayServer
import com.github.aivanovski.testswithme.android.domain.driverServer.controllers.JobController
import com.github.aivanovski.testswithme.android.domain.driverServer.controllers.StartTestController
import com.github.aivanovski.testswithme.android.domain.driverServer.controllers.StatusController
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerInteractor
import com.github.aivanovski.testswithme.android.domain.flow.PathResolver
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProviderImpl
import com.github.aivanovski.testswithme.android.domain.usecases.GetCurrentJobUseCase
import com.github.aivanovski.testswithme.android.domain.usecases.GetExternalApplicationDataUseCase
import com.github.aivanovski.testswithme.android.domain.usecases.IsLoggedInUseCase
import com.github.aivanovski.testswithme.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.testswithme.android.presentation.StartArgs
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.BottomSheetMenu
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.BottomSheetMenuViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.cells.BottomSheetMenuCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.flow.FlowInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.flow.FlowViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.FlowCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.GroupEditorInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.GroupEditorViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groups.GroupsInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.groups.GroupsViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.GroupsCellModelFactory
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.login.LoginInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.login.LoginViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.ProjectDashboardInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.ProjectDashboardViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.ProjectDashboardCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.ProjectEditorInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.ProjectEditorViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projects.ProjectsInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.projects.ProjectsViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projects.cells.ProjectsCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.SettingsInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.settings.SettingsViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.settings.cells.SettingsCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.TestRunInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.TestRunViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.TestRunCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.TestRunsInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.TestRunsViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.cells.TestRunsCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.UploadTestInteractor
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.UploadTestViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestScreenArgs
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import timber.log.Timber

object AndroidAppModule {

    val module = module {
        single<Settings> { SettingsImpl(get()) }
        single<ResourceProvider> { ResourceProviderImpl(get()) }
        single<FileCache> { FileCacheImpl(get()) }
        singleOf(::ThemeProvider)
        singleOf(::VersionParser)
        singleOf(::JsonSerializer)
        singleOf(::PathResolver)

        // Database
        single { AppDatabase.buildDatabase(get()) }
        single { provideStepEntryDao(get()) }
        single { provideFlowEntryDao(get()) }
        single { provideRunnerEntryDao(get()) }
        single { provideExecutionEntryDao(get()) }
        single { provideJobHistoryEntryDao(get()) }
        single { provideProjectEntryDao(get()) }

        // Network
        single { provideHttpRequestExecutor(get()) }
        singleOf(::ApiClient)

        // Repositories
        singleOf(::FlowRepository)
        singleOf(::JobRepository)
        singleOf(::StepRunRepository)
        singleOf(::ProjectRepository)
        singleOf(::FlowRunRepository)
        singleOf(::UserRepository)
        singleOf(::GroupRepository)

        // UseCases
        singleOf(::ParseFlowFileUseCase)
        singleOf(::GetCurrentJobUseCase)
        singleOf(::GetExternalApplicationDataUseCase)
        singleOf(::IsLoggedInUseCase)

        // Interactors
        singleOf(::LoginInteractor)
        singleOf(::GroupsInteractor)
        singleOf(::FlowInteractor)
        singleOf(::ProjectsInteractor)
        singleOf(::TestRunsInteractor)
        singleOf(::TestRunInteractor)
        singleOf(::UploadTestInteractor)
        singleOf(::ProjectDashboardInteractor)
        singleOf(::ProjectEditorInteractor)
        singleOf(::GroupEditorInteractor)
        singleOf(::GatewayReceiverInteractor)
        singleOf(::SettingsInteractor)

        // Flow runner
        singleOf(::FlowRunnerInteractor)

        // Gateway server
        singleOf(::GatewayServer)
        singleOf(::StatusController)
        singleOf(::StartTestController)
        singleOf(::JobController)

        // Cell factories
        singleOf(::FlowCellFactory)
        singleOf(::GroupsCellModelFactory)
        singleOf(::ProjectsCellFactory)
        singleOf(::TestRunsCellFactory)
        singleOf(::TestRunCellFactory)
        singleOf(::ProjectDashboardCellFactory)
        singleOf(::SettingsCellFactory)
        singleOf(::BottomSheetMenuCellFactory)

        // ViewModels
        factory { (menu: BottomSheetMenu) -> BottomSheetMenuViewModel(get(), menu) }
        factory { (router: Router, args: StartArgs) ->
            RootViewModel(
                get(),
                get(),
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router) ->
            LoginViewModel(get(), get(), vm, router)
        }
        factory { (vm: RootViewModel, router: Router, args: GroupsScreenArgs) ->
            GroupsViewModel(
                get(),
                get(),
                get(),
                vm,
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router, args: FlowScreenArgs) ->
            FlowViewModel(
                get(),
                get(),
                get(),
                vm,
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router) ->
            ProjectsViewModel(
                get(),
                get(),
                get(),
                vm,
                router
            )
        }
        factory { (vm: RootViewModel, router: Router) ->
            TestRunsViewModel(
                get(),
                get(),
                get(),
                vm,
                router
            )
        }
        factory { (vm: RootViewModel, router: Router, args: TestRunScreenArgs) ->
            TestRunViewModel(
                get(),
                get(),
                get(),
                vm,
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router, args: UploadTestScreenArgs) ->
            UploadTestViewModel(
                get(),
                get(),
                vm,
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router, args: ProjectDashboardScreenArgs) ->
            ProjectDashboardViewModel(
                get(),
                get(),
                get(),
                vm,
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router, args: ProjectEditorArgs) ->
            ProjectEditorViewModel(
                get(),
                get(),
                vm,
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router, args: GroupEditorScreenArgs) ->
            GroupEditorViewModel(
                get(),
                get(),
                vm,
                router,
                args
            )
        }
        factory { (vm: RootViewModel, router: Router) ->
            SettingsViewModel(
                get(),
                get(),
                get(),
                get(),
                vm,
                router
            )
        }
    }

    private fun provideStepEntryDao(db: AppDatabase): StepEntryDao = db.stepEntryDao

    private fun provideFlowEntryDao(db: AppDatabase): FlowEntryDao = db.flowEntryDao

    private fun provideRunnerEntryDao(db: AppDatabase): JobDao = db.runnerEntryDao

    private fun provideExecutionEntryDao(db: AppDatabase): LocalStepRunDao = db.executionDataDao

    private fun provideJobHistoryEntryDao(db: AppDatabase): JobHistoryDao = db.jobHistoryDao

    private fun provideProjectEntryDao(db: AppDatabase): ProjectEntryDao = db.projectEntryDao

    private fun provideHttpRequestExecutor(settings: Settings): HttpRequestExecutor {
        return HttpRequestExecutor(
            client = HttpClient(OkHttp) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Timber.d(message)
                        }
                    }
                    level = LogLevel.INFO
                }

                if (settings.isSslVerificationDisabled) {
                    Timber.i("SSL certificate validation disabled")
                    disableSslVerification()
                }
            }
        )
    }

    private fun HttpClientConfig<OkHttpConfig>.disableSslVerification() {
        @SuppressLint("CustomX509TrustManager")
        val trustAllCerts = object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }

        val sslSocketFactory = SSLContext.getInstance("SSL")
            .apply {
                init(null, arrayOf(trustAllCerts), SecureRandom())
            }
            .socketFactory

        engine {
            config {
                sslSocketFactory(sslSocketFactory, trustAllCerts)
                hostnameVerifier(hostnameVerifier = { _, _ -> true })
            }
        }
    }
}