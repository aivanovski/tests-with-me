package com.github.aivanovski.testswithme.web.presentation.jobs

import arrow.core.raise.either
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.web.data.repository.SyncResultRepository
import com.github.aivanovski.testswithme.web.data.repository.TestSourceRepository
import com.github.aivanovski.testswithme.web.di.GlobalInjector
import com.github.aivanovski.testswithme.web.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.web.domain.sync.GetTestSourcesToSyncUseCase
import com.github.aivanovski.testswithme.web.domain.sync.FlowSynchronizer
import com.github.aivanovski.testswithme.web.entity.SyncResult
import com.github.aivanovski.testswithme.web.entity.Timestamp
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.extensions.isNecessaryToSync
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory

class SyncFlowsWithRepositoryJob : Job {

    private val getSourcesUseCase: GetTestSourcesToSyncUseCase by inject()
    private val testSourceRepository: TestSourceRepository by inject()
    private val syncResultRepository: SyncResultRepository by inject()

    override fun execute(context: JobExecutionContext?) {
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

                val startTimestamp = Timestamp.now()
                val syncUid = Uid.generate()

                val synchronizer = FlowSynchronizer(
                    projectRepository = GlobalInjector.get(),
                    groupRepository = GlobalInjector.get(),
                    flowRepository = GlobalInjector.get(),
                    testSourceRepository = GlobalInjector.get(),
                    fileSystemProvider = GlobalInjector.get(),
                    referenceResolver = GlobalInjector.get(),
                    cloneRepoUseCase = GlobalInjector.get(),
                    getLastCommitUseCase = GlobalInjector.get(),
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
                                index = index
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
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SyncFlowsWithRepositoryJob::class.java)
    }
}