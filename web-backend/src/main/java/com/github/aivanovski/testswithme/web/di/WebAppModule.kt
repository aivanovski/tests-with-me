package com.github.aivanovski.testswithme.web.di

import com.github.aivanovski.testswithme.data.resources.ResourceProvider
import com.github.aivanovski.testswithme.data.resources.ResourceProviderImpl
import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.data.database.dao.FlowDao
import com.github.aivanovski.testswithme.web.data.database.dao.FlowRunDao
import com.github.aivanovski.testswithme.web.data.database.dao.GroupDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.data.database.dao.UserDao
import com.github.aivanovski.testswithme.web.data.file.FileStorage
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.data.repository.UserRepository
import com.github.aivanovski.testswithme.web.domain.PathResolver
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.domain.usecases.ValidateEmailUseCase
import com.github.aivanovski.testswithme.web.presentation.controller.FlowController
import com.github.aivanovski.testswithme.web.presentation.controller.FlowRunController
import com.github.aivanovski.testswithme.web.presentation.controller.GroupController
import com.github.aivanovski.testswithme.web.presentation.controller.LoginController
import com.github.aivanovski.testswithme.web.presentation.controller.ProjectController
import com.github.aivanovski.testswithme.web.presentation.controller.SignUpController
import com.github.aivanovski.testswithme.web.presentation.controller.UserController
import org.koin.dsl.module

object WebAppModule {

    val module = module {
        // core
        single<ResourceProvider> { ResourceProviderImpl(WebAppModule::class) }
        single { FileStorage() }

        // Database
        single { AppDatabase() }
        single { FlowDao(get()) }
        single { UserDao(get()) }
        single { ProjectDao(get()) }
        single { GroupDao(get()) }
        single { FlowRunDao(get()) }

        // Repositories
        single { UserRepository(get()) }
        single<FlowRepository> { FlowRepository(get(), get(), get(), get()) }
        single<ProjectRepository> { ProjectRepository(get()) }
        single { FlowRunRepository(get(), get(), get(), get()) }
        single { GroupRepository(get(), get()) }

        // UseCases
        single { ValidateEmailUseCase() }
        single { PathResolver(get(), get()) }

        // Services
        single { AuthService(get()) }

        // Controllers
        single { LoginController(get()) }
        single { SignUpController(get(), get(), get()) }
        single { FlowController(get(), get()) }
        single { ProjectController(get(), get()) }
        single { FlowRunController(get(), get(), get()) }
        single { UserController(get()) }
        single { GroupController(get(), get(), get()) }
    }
}