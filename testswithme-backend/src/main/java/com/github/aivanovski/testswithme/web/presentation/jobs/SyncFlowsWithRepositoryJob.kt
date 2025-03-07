package com.github.aivanovski.testswithme.web.presentation.jobs

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.web.data.repository.SyncResultRepository
import com.github.aivanovski.testswithme.web.data.repository.TestSourceRepository
import com.github.aivanovski.testswithme.web.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.web.domain.sync.FlowSynchronizer
import com.github.aivanovski.testswithme.web.domain.usecases.GetRemoteRepositoryLastCommitUseCase
import com.github.aivanovski.testswithme.web.domain.usecases.GetTestSourcesToSyncUseCase
import com.github.aivanovski.testswithme.web.domain.usecases.ParseGithubRepositoryUrlUseCase
import com.github.aivanovski.testswithme.web.entity.SyncResult
import com.github.aivanovski.testswithme.web.entity.Timestamp
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.extensions.isNecessaryToSync
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory

class SyncFlowsWithRepositoryJob : Job {

    private val getSourcesUseCase: GetTestSourcesToSyncUseCase by inject()
    private val testSourceRepository: TestSourceRepository by inject()
    private val syncResultRepository: SyncResultRepository by inject()
    private val getLastCommitUseCase: GetRemoteRepositoryLastCommitUseCase by inject()
    private val parseUrlUseCase: ParseGithubRepositoryUrlUseCase by inject()

    override fun execute(context: JobExecutionContext?) {
        runBlocking {
            checkAndSync()
        }
    }

    private suspend fun checkAndSync(): Either<AppException, Unit> =
        either {
            val sourceUids = getSourcesUseCase.getTestSourcesToSync()
                .bind()
                .map { source -> source.uid }

            for (sourceUid in sourceUids) {
                val source = testSourceRepository.getByUid(sourceUid).bind()
                if (!source.isNecessaryToSync()) {
                    logger.debug(
                        "Skip sync for repository {} as it already up to date",
                        source.repositoryUrl
                    )
                    continue
                }

                if (source.lastCommitHash != null) {
                    val repo = parseUrlUseCase.parseRepositoryUrl(source.repositoryUrl).bind()
                    val lastCommitHash = getLastCommitUseCase.getLastCommitHash(repo).bind()

                    if (lastCommitHash == source.lastCommitHash) {
                        logger.debug(
                            "Skip sync for repository {} as no new commits",
                            source.repositoryUrl
                        )

                        testSourceRepository.update(
                            source.copy(
                                lastCheckTimestamp = Timestamp.now(),
                                isForceSyncFlag = false
                            )
                        )

                        continue
                    }
                }

                val startTimestamp = Timestamp.now()
                val syncUid = Uid.generate()

                val synchronizer = FlowSynchronizer(
                    syncUid = syncUid,
                    source = source
                )

                val syncResult = synchronizer.sync()

                synchronizer.cleanUp()

                val endTimestamp = Timestamp.now()

                if (syncResult.isRight()) {
                    val (updatedSource, processedItems) = syncResult.unwrap()

                    logger.debug(
                        "Repository {} synced successfully: processedItems={}, commitSha={}",
                        source.repositoryUrl,
                        processedItems.size,
                        updatedSource.lastCommitHash
                    )
                    processedItems.onEach { item -> logger.debug("    {}", item) }

                    syncResultRepository.add(
                        result = SyncResult(
                            uid = syncUid,
                            testSourceUid = source.uid,
                            startTimestamp = startTimestamp,
                            endTimestamp = endTimestamp,
                            isSuccess = true
                        ),
                        items = processedItems.mapIndexed { index, item ->
                            item.copy(
                                itemIndex = index
                            )
                        }
                    )
                } else {
                    val error = syncResult.unwrapError()
                    logger.error("Sync failed with error: ", error)

                    syncResultRepository.add(
                        result = SyncResult(
                            uid = syncUid,
                            testSourceUid = source.uid,
                            startTimestamp = startTimestamp,
                            endTimestamp = endTimestamp,
                            isSuccess = false
                        ),
                        items = emptyList()
                    )
                }
            }
        }

    companion object {
        private val logger = LoggerFactory.getLogger(SyncFlowsWithRepositoryJob::class.java)
    }
}