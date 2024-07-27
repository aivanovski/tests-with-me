package com.github.aivanovski.testswithme.android.domain.flow.reporter

import com.github.aivanovski.testswithme.flow.runner.reporter.FlowReporter
import com.github.aivanovski.testswithme.utils.Logger

class TimberFlowReporter(
    logger: Logger
) : FlowReporter(
    logger = logger,
    flowTransformer = ShortNameTransformer()
)