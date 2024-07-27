package com.github.aivanovski.testwithme.android.di

import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.testwithme.android.data.api.ApiClient
import com.github.aivanovski.testwithme.android.data.api.HttpRequestExecutor
import com.github.aivanovski.testwithme.android.data.db.AppDatabase
import com.github.aivanovski.testwithme.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.testwithme.android.data.db.dao.JobDao
import com.github.aivanovski.testwithme.android.data.db.dao.JobHistoryDao
import com.github.aivanovski.testwithme.android.data.db.dao.LocalStepRunDao
import com.github.aivanovski.testwithme.android.data.db.dao.ProjectEntryDao
import com.github.aivanovski.testwithme.android.data.db.dao.StepEntryDao
import com.github.aivanovski.testwithme.android.data.file.FileCache
import com.github.aivanovski.testwithme.android.data.file.FileCacheImpl
import com.github.aivanovski.testwithme.android.data.repository.FlowRepository
import com.github.aivanovski.testwithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testwithme.android.data.repository.GroupRepository
import com.github.aivanovski.testwithme.android.data.repository.JobRepository
import com.github.aivanovski.testwithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testwithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testwithme.android.data.repository.UserRepository
import com.github.aivanovski.testwithme.android.data.settings.Settings
import com.github.aivanovski.testwithme.android.data.settings.SettingsImpl
import com.github.aivanovski.testwithme.android.domain.VersionParser
import com.github.aivanovski.testwithme.android.domain.flow.FlowRunnerInteractor
import com.github.aivanovski.testwithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.domain.resources.ResourceProviderImpl
import com.github.aivanovski.testwithme.android.domain.usecases.GetCurrentJobUseCase
import com.github.aivanovski.testwithme.android.domain.usecases.GetExternalApplicationDataUseCase
import com.github.aivanovski.testwithme.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.testwithme.android.presentation.StartArgs
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testwithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testwithme.android.presentation.screens.flow.FlowInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.flow.FlowViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.flow.cells.FlowCellFactory
import com.github.aivanovski.testwithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testwithme.android.presentation.screens.groups.GroupsInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.groups.GroupsViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.GroupsCellModelFactory
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.GroupsCellViewModelFactory
import com.github.aivanovski.testwithme.android.presentation.screens.groups.model.GroupsScreenArgs
import com.github.aivanovski.testwithme.android.presentation.screens.login.LoginInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.login.LoginViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.ProjectDashboardInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.ProjectDashboardViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.cells.ProjectDashboardCellFactory
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.model.ProjectDashboardScreenArgs
import com.github.aivanovski.testwithme.android.presentation.screens.projectEditor.ProjectEditorInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.projectEditor.ProjectEditorViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.projectEditor.model.ProjectEditorArgs
import com.github.aivanovski.testwithme.android.presentation.screens.projects.ProjectsInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.projects.ProjectsViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.ProjectsCellFactory
import com.github.aivanovski.testwithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.TestRunInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.TestRunViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.TestRunCellFactory
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.model.TestRunScreenArgs
import com.github.aivanovski.testwithme.android.presentation.screens.testRuns.TestRunsInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.testRuns.TestRunsViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.testRuns.cells.TestRunsCellFactory
import com.github.aivanovski.testwithme.android.presentation.screens.uploadTest.UploadTestInteractor
import com.github.aivanovski.testwithme.android.presentation.screens.uploadTest.UploadTestViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.uploadTest.model.UploadTestScreenArgs
import com.github.aivanovski.testwithme.flow.driver.Driver
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.koin.dsl.module
import timber.log.Timber

object AndroidAppModule {

    val module = module {
        single<Settings> { SettingsImpl(get()) }
        single<ResourceProvider> { ResourceProviderImpl(get()) }
        single<FileCache> { FileCacheImpl(get()) }
        single { ThemeProvider() }
        single { VersionParser() }

        // Database
        single { AppDatabase.buildDatabase(get()) }
        single { provideStepEntryDao(get()) }
        single { provideFlowEntryDao(get()) }
        single { provideRunnerEntryDao(get()) }
        single { provideExecutionEntryDao(get()) }
        single { provideJobHistoryEntryDao(get()) }
        single { provideProjectEntryDao(get()) }

        // Network
        single { provideHttpRequestExecutor() }
        single { ApiClient(get(), get()) }

        // Repositories
        single { FlowRepository(get(), get(), get(), get(), get(), get()) }
        single { JobRepository(get(), get()) }
        single { StepRunRepository(get(), get()) }
        single { ProjectRepository(get(), get()) }
        single { FlowRunRepository(get()) }
        single { UserRepository(get()) }
        single { GroupRepository(get()) }

        // UseCases
        single { ParseFlowFileUseCase() }
        single { GetCurrentJobUseCase(get()) }
        single { GetExternalApplicationDataUseCase(get()) }

        // Interactors
        single {
            FlowRunnerInteractor(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        single { LoginInteractor(get(), get()) }
        single { GroupsInteractor(get(), get(), get()) }
        single { FlowInteractor(get(), get(), get(), get(), get(), get(), get()) }
        single { ProjectsInteractor(get()) }
        single { TestRunsInteractor(get(), get(), get(), get()) }
        single { TestRunInteractor(get(), get()) }
        single { UploadTestInteractor(get(), get(), get(), get()) }
        single { ProjectDashboardInteractor(get(), get(), get(), get(), get()) }
        single { ProjectEditorInteractor(get()) }

        // Cell factories
        single { FlowCellFactory(get(), get()) }
        single { GroupsCellModelFactory(get()) }
        single { GroupsCellViewModelFactory() }
        single { ProjectsCellFactory(get()) }
        single { TestRunsCellFactory(get()) }
        single { TestRunCellFactory(get()) }
        single { ProjectDashboardCellFactory(get(), get()) }

        single { (driver: Driver<AccessibilityNodeInfo>) ->
            FlowRunnerManager(
                get(),
                get(),
                get(),
                driver
            )
        }

        // ViewModels
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
    }

    private fun provideStepEntryDao(db: AppDatabase): StepEntryDao = db.stepEntryDao

    private fun provideFlowEntryDao(db: AppDatabase): FlowEntryDao = db.flowEntryDao

    private fun provideRunnerEntryDao(db: AppDatabase): JobDao = db.runnerEntryDao

    private fun provideExecutionEntryDao(db: AppDatabase): LocalStepRunDao = db.executionDataDao

    private fun provideJobHistoryEntryDao(db: AppDatabase): JobHistoryDao = db.jobHistoryDao

    private fun provideProjectEntryDao(db: AppDatabase): ProjectEntryDao = db.projectEntryDao

    private fun provideHttpRequestExecutor(): HttpRequestExecutor {
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
            }
        )
    }
}