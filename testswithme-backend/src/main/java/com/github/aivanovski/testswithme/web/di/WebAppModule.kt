package com.github.aivanovski.testswithme.web.di

import com.github.aivanovski.testswithme.domain.validation.ValidateEmailUseCase
import com.github.aivanovski.testswithme.web.data.arguments.ArgumentParser
import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.data.database.dao.FlowDao
import com.github.aivanovski.testswithme.web.data.database.dao.FlowRunDao
import com.github.aivanovski.testswithme.web.data.database.dao.GroupDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.data.database.dao.UserDao
import com.github.aivanovski.testswithme.web.data.file.FileStorage
import com.github.aivanovski.testswithme.web.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.web.data.file.FileSystemProviderImpl
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.data.repository.UserRepository
import com.github.aivanovski.testswithme.web.domain.AccessResolver
import com.github.aivanovski.testswithme.web.domain.ReferenceResolver
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.domain.usecases.GetSslKeyStoreUseCase
import com.github.aivanovski.testswithme.web.presentation.controller.CORSController
import com.github.aivanovski.testswithme.web.presentation.controller.FlowController
import com.github.aivanovski.testswithme.web.presentation.controller.FlowRunController
import com.github.aivanovski.testswithme.web.presentation.controller.GroupController
import com.github.aivanovski.testswithme.web.presentation.controller.LoginController
import com.github.aivanovski.testswithme.web.presentation.controller.ProjectController
import com.github.aivanovski.testswithme.web.presentation.controller.SignUpController
import com.github.aivanovski.testswithme.web.presentation.controller.UserController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object WebAppModule {

    val module = module {
        // core
        single<FileSystemProvider> { FileSystemProviderImpl() }
        singleOf(::FileStorage)
        singleOf(::ReferenceResolver)
        singleOf(::AccessResolver)
        singleOf(::ArgumentParser)

        // Database
        singleOf(::AppDatabase)
        singleOf(::FlowDao)
        singleOf(::UserDao)
        singleOf(::ProjectDao)
        singleOf(::GroupDao)
        singleOf(::FlowRunDao)

        // Repositories
        singleOf(::UserRepository)
        singleOf(::FlowRunRepository)
        singleOf(::GroupRepository)
        singleOf(::FlowRepository)
        singleOf(::ProjectRepository)

        // UseCases
        singleOf(::ValidateEmailUseCase)
        singleOf(::GetSslKeyStoreUseCase)

        // Services
        singleOf(::AuthService)

        // Controllers
        singleOf(::CORSController)
        singleOf(::LoginController)
        singleOf(::SignUpController)
        singleOf(::FlowController)
        singleOf(::ProjectController)
        singleOf(::FlowRunController)
        singleOf(::UserController)
        singleOf(::GroupController)
    }
}