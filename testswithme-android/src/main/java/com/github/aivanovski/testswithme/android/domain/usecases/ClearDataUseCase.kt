package com.github.aivanovski.testswithme.android.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testswithme.android.entity.exception.AppException

class ClearDataUseCase(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val flowRepository: FlowRepository,
    private val authRepository: AuthRepository,
    private val jobRepository: JobRepository,
    private val runRepository: StepRunRepository
) {

    fun clearUserData(): Either<AppException, Unit> =
        either {
            projectRepository.clear()
            groupRepository.clear()
            flowRepository.clear()
            jobRepository.clear()
            runRepository.clear()
            authRepository.logout()
        }
}